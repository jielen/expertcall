package com.ufgov.server;

import java.util.Random;

import org.xvolks.jnative.JNative;
import org.xvolks.jnative.Type;

import com.ufgov.util.ApplicationContext;
import com.ufgov.util.StackUtil;

public class RingServer extends Thread {
  /**
   * 启动扫描线程
   */
  private int randomNum = 0;

  public void run() {

    randomNum = new Random().nextInt(1000);

    while (true) {
      try {
        //LogWritter.println("*********************监听呼入电话开始*********************", randomNum);
        scan();
        //LogWritter.println("*********************监听呼入电话结束*********************", randomNum);
        Thread.sleep(3000);
      } catch (Exception e) {
        //LogWritter.println("*********************监听呼入电话结束*********************", randomNum);
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e1) {
        }
        e.printStackTrace();
        //LogWritter.println(e.getMessage(), randomNum);
      }
    }
  }

  public void scan() throws Exception {
    int num = Integer.parseInt(ApplicationContext.singleton().getValueAsString("ringThread"));
    JNative n = null;
    String ports[] = ApplicationContext.singleton().getValueAsString("port").split(",");
    int port = -1;

    for (int ch = 0; ch < num; ch++) {
      //查看是否有振铃
      //LogWritter.println("*********************监听外线模块" + ch + "*********************", randomNum);
      n = new JNative("SHP_A3", "SsmGetChState");
      n.setRetVal(Type.INT);
      n.setParameter(0, Type.INT, ch + "");
      n.invoke();

      if (n.getRetVal().equals("2")) {//有振铃
        //判断是否有别的线程已经处理该外线模块
        if (StackUtil.stackRing.contains(ch))
          continue;
        StackUtil.pushRing(ch);

        for (int i = 0; i < ports.length; i++) {
          n = new JNative("SHP_A3", "SsmGetChState");
          n.setRetVal(Type.INT);
          n.setParameter(0, Type.INT, ports[i]);
          n.invoke();
          if (n.getRetVal().equals("0"))
            port = Integer.parseInt(ports[i]);
        }
        //LogWritter.println("*********************外线模块" + ch + "有震铃，选择空闲坐席" + port + "*********************",
        // randomNum);
        //当无空闲坐席时通知对方繁忙稍后再拨 modified by mengw 20100906
        if (port == -1) {
          n = new JNative("SHP_A3", "SsmPickup");
          n.setRetVal(Type.INT);
          n.setParameter(0, Type.INT, ch + "");
          n.invoke();

          n = new JNative("PlayText", "LoadHZK");
          n.setRetVal(Type.INT);
          n.invoke();

          n = new JNative("PlayText", "PlayText");
          n.setRetVal(Type.INT);
          n.setParameter(0, Type.INT, ch + "");
          n.setParameter(1, Type.STRING, "您所拨打的电话繁忙，请稍后再拨");
          n.invoke();
          Thread.sleep(10000);

          n = new JNative("SHP_A3", "SsmHangup");
          n.setRetVal(Type.INT);
          n.setParameter(0, Type.INT, ch + "");
          n.invoke();
          //线程结束释放资源 modified by mengw 20100906
          if (StackUtil.stackRing.contains(ch))
            StackUtil.removeRing(ch);
          return;
        }
        //LogWritter.println("*********************外线模块" + ch + "有震铃，呼叫坐席*********************", randomNum);
        n = new JNative("SHP_A3", "SsmStartRing");
        n.setRetVal(Type.INT);
        n.setParameter(0, Type.INT, port + "");
        n.invoke();
        //监听发送振铃的坐席通道状态
        int sign = 0;
        while (true) {
          //外线通道挂机后退出
          n = new JNative("SHP_A3", "SsmGetChState");
          n.setRetVal(Type.INT);
          n.setParameter(0, Type.INT, ch + "");
          n.invoke();
          if (n.getRetVal().equals("0") || n.getRetVal().equals("7")) {
            n = new JNative("SHP_A3", "SsmStopRing");
            n.setRetVal(Type.INT);
            n.setParameter(0, Type.INT, port + "");
            n.invoke();

            n = new JNative("SHP_A3", "SsmHangup");
            n.setRetVal(Type.INT);
            n.setParameter(0, Type.INT, ch + "");
            n.invoke();
            // LogWritter.println("*********************外线通道挂断结束*********************", randomNum);
            break;

          }
          //坐席无人接听30秒超时
          if (sign >= 30000) {
            n = new JNative("SHP_A3", "SsmStopRing");
            n.setRetVal(Type.INT);
            n.setParameter(0, Type.INT, port + "");
            n.invoke();

            n = new JNative("SHP_A3", "SsmHangup");
            n.setRetVal(Type.INT);
            n.setParameter(0, Type.INT, ch + "");
            n.invoke();
            //LogWritter.println("*********************呼叫坐席超时结束*********************", randomNum);
            break;
          }
          n = new JNative("SHP_A3", "SsmGetChState");
          n.setRetVal(Type.INT);
          n.setParameter(0, Type.INT, port + "");
          n.invoke();
          if (n.getRetVal().equals("1")) {
            //坐席通道已摘机
            n = new JNative("SHP_A3", "SsmGetChState");
            n.setRetVal(Type.INT);
            n.setParameter(0, Type.INT, ch + "");
            n.invoke();
            if (n.getRetVal().equals("2")) {
              //外线通道摘机
              n = new JNative("SHP_A3", "SsmPickup");
              n.setRetVal(Type.INT);
              n.setParameter(0, Type.INT, ch + "");
              n.invoke();
              //外线通道和坐席通道链接
              // LogWritter.println("*********************外线通道和坐席通道链接*********************", randomNum);
              n = new JNative("SHP_A3", "SsmTalkWith");
              n.setRetVal(Type.INT);
              n.setParameter(0, Type.INT, ch + "");
              n.setParameter(1, Type.INT, port + "");
              n.invoke();

              while (true) {
                //获取外线挂机状态
                n = new JNative("SHP_A3", "SsmGetChState");
                n.setRetVal(Type.INT);
                n.setParameter(0, Type.INT, ch + "");
                n.invoke();
                if (n.getRetVal().equals("7") || n.getRetVal().equals("0")) {
                  n = new JNative("SHP_A3", "SsmHangup");
                  n.setRetVal(Type.INT);
                  n.setParameter(0, Type.INT, port + "");
                  n.invoke();
                  //LogWritter.println("*********************外线通道挂机通话结束*********************", randomNum);
                  break;
                }
                //获取坐席挂机状态
                n = new JNative("SHP_A3", "SsmGetChState");
                n.setRetVal(Type.INT);
                n.setParameter(0, Type.INT, port + "");
                n.invoke();
                if (n.getRetVal().equals("0") || n.getRetVal().equals("7")) {
                  n = new JNative("SHP_A3", "SsmHangup");
                  n.setRetVal(Type.INT);
                  n.setParameter(0, Type.INT, ch + "");
                  n.invoke();
                  //LogWritter.println("*********************坐席通道挂机通话结束*********************", randomNum);
                  break;
                }
                Thread.sleep(10);
              }
              n = new JNative("SHP_A3", "SsmStopRing");
              n.setRetVal(Type.INT);
              n.setParameter(0, Type.INT, port + "");
              n.invoke();
              break;
            } else {
              n = new JNative("SHP_A3", "SsmStopRing");
              n.setRetVal(Type.INT);
              n.setParameter(0, Type.INT, port + "");
              n.invoke();
              break;
            }

          }
          sign += 10;
          Thread.sleep(10);
        }
        //线程结束释放资源modified by mengw 20100906
        if (StackUtil.stackRing.contains(ch))
          StackUtil.removeRing(ch);
      }
    }
  }
}
