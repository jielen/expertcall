package com.ufgov.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;

import com.ufgov.ssm.SSMFactory;
import com.ufgov.tts.TTSFactory;
import com.ufgov.util.ApplicationContext;

public class ServiceContext {

  private static Logger logger = Logger.getLogger(ServiceContext.class);

  public static List<CallServer> callServerList = new ArrayList<CallServer>();
  
  public static void main(String[] args) {
	 
    ServiceContext serviceContext = new ServiceContext();
    serviceContext.init();
    
    new BillServer().start();
    
    //外拨电话线数量
	final int linesNum = Integer.parseInt(ApplicationContext.singleton().getValueAsString("callThread"));
	
	Thread th = new Thread() {
		public void run() {
			TTSFactory tsf = new TTSFactory();
			tsf.init();			
			try {
				for (int i = 0; i < linesNum; i++) {					
					CallServer call = new CallServer();
					call.setTsf(tsf);
					call.setthreadNum(i);
					call.start();
          callServerList.add(call);
					sleep(30000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error("启动呼叫进程错误!\n" + e.getMessage(), e);				
			}finally{
			}  
		}
	};
	th.start();
  }

  public void init() {
    try {
      initSsmCard();
    } catch (Exception e) {
      logger.info("语音卡初始化失败。" + e.getMessage());
      System.exit(0);
    }
//    boolean flag = CallServer.init();
//    if (!flag) {
//      logger.error("语音呼叫系统初始化失败。");
//      System.exit(0);
//    }
    //清除运行过程中产生的语音文件
    clearWavs(new File(CallServer.VOICE_DIR));
    logger.info("系统初始化完毕。");
  }

  private boolean clearWavs(File dir) {
		if (dir==null || !dir.exists()) {
			return false;			
		}
		if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = clearWavs(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
}

  private void initSsmCard() throws NativeException, IllegalAccessException {
    System.loadLibrary("SHP_A3");
    String shConfig = ApplicationContext.singleton().getValueAsString("shConfig");
    String shIndex = ApplicationContext.singleton().getValueAsString("shIndex");
    int initState = SSMFactory.ssmStartCti(shConfig, shIndex);
    if (initState == 0) {
      logger.info("语音卡初始化板卡成功。");
    } else if (initState == -2) {
      logger.info("您已经启动了一个语音卡实例，请关闭。");
      System.exit(0);
    } else if (initState == -1) {
      logger.info("语音卡初始化失败。" + getLastErrMsg());
      System.exit(0);
    }
  }

  public String getLastErrMsg() throws NativeException, IllegalAccessException {
    Pointer pointer = SSMFactory.creatPointer(40);
    SSMFactory.ssmGetLastErrMsg(pointer);
    String errMeg = pointer.getAsString();
    pointer.dispose();
    return errMeg;
  }
}
