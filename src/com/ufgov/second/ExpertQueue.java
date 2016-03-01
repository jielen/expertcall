/**
 * 
 */
package com.ufgov.second;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 专家队列类 1。存储等候拨打电话的专家 2.指明专家与抽取单的关系 3.通过同步的方式，往队列里加入新专家 4.通过同步的方式，从队列里获取专家
 * @author Administrator
 */
public class ExpertQueue {
  private static Logger logger = Logger.getLogger(ExpertQueue.class);

  /**
   * 抽取单列表 保存抽取单编号
   */
//  private List<String> emBillLst = new ArrayList<String>();

  /**
   * 等候队列
   */
  private List<ExpertCallInfo> waitingExpertLst = new ArrayList<ExpertCallInfo>();

  /**
   * 通话队列
   */
  private List<ExpertCallInfo> callingExpertLst = new ArrayList<ExpertCallInfo>();

  /**
   * 从等候队列中获取一个专家，并把该专家转入通话队列中
   * @param billCode 抽取单编号，如果指定了这个值，则获取这个单据的等待专家，如果没有指定(为null),或者没有找过对应的专家，则获取排头第一个等候的专家
   * @return
   */
  public synchronized ExpertCallInfo pop(String billCode) {
    while(waitingExpertLst.size()==0){
      try {
        wait();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
//        e.printStackTrace();
        logger.error("获取等待的专家异常！"+e.getMessage(),e);
      }
    }
    //获取一个等待的专家
    if (billCode == null) {
      ExpertCallInfo expert = waitingExpertLst.get(0);
      callingExpertLst.add(expert);
      waitingExpertLst.remove(0);
      return expert;
    } else {
      for (int i = 0; i < waitingExpertLst.size(); i++) {
        ExpertCallInfo expert = waitingExpertLst.get(i);
        if (expert.getBillCode().equals(billCode)) {
          callingExpertLst.add(expert);
          waitingExpertLst.remove(i);
          return expert;
        }
      }
      ExpertCallInfo expert = waitingExpertLst.get(0);
      callingExpertLst.add(expert);
      waitingExpertLst.remove(0);
      return expert;
    }
  }
  /**
   * 往等候队列中添加一个专家
   * @param expert
   */
  public synchronized void push(ExpertCallInfo expert) {
    if(expert==null){
      return;
    }
    if(waitingExpertLst.contains(expert)){
      return;
    }
    waitingExpertLst.add(expert);
    notifyAll();
  }
  /**
   * 完成通话的专家（包括打通了、未通的、空号等）,将该专家从通话队列中删除
   * @param expert
   */
  public synchronized void completeCalling(ExpertCallInfo expert) {
    if(expert==null){
      return;
    }
    for(int i=0;i<callingExpertLst.size();i++){
      ExpertCallInfo e=callingExpertLst.get(i);
      if(e.getObjid().equals(expert.getObjid())){
        callingExpertLst.remove(i);
      }
    }
  }
  
  /**
   * 是否存在某抽取单的专家存在于等候队列或者通话队列中
   * @param billCode
   * @return
   */
  private synchronized boolean existsBill(String billCode) {
    if(billCode==null){
      return false;
    }
    for (int i = 0; i < waitingExpertLst.size(); i++) {
      ExpertCallInfo expert = waitingExpertLst.get(i);
      if(billCode.equals(expert.getBillCode())){
        return true;
      }
    }
    for(int i=0;i<callingExpertLst.size();i++){
      ExpertCallInfo e=callingExpertLst.get(i);
      if(billCode.equals(e.getBillCode())){
        return true;
      }
    }
    return false;
  }
  
  public synchronized void removeWaitingExperts(String billCode) {
    for (int i = waitingExpertLst.size()-1; i >=0; i--) {
      ExpertCallInfo expert = waitingExpertLst.get(i);
      if(expert.getBillCode().equals(billCode)){
        waitingExpertLst.remove(i);
      }
    }
  }

}
