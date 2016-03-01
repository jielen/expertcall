/**
 * 
 */
package com.ufgov.second;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ufgov.server.BillServer;
import com.ufgov.util.DAOFactory;
import com.ufgov.util.EmCallException;

/**
 * 专家搜索类
 * 1。检查是否有抽取单，
 * 1.1如果有抽取单，判断专家等候队列中是否有包含了这个抽取单
 * 1.1.1 如果没有， 检查专家抽取情况
 * 1.1.1.1 如果专家已经够了，则更新抽取单据为抽取完成
 * 1.1.1.2 如果专家不够，从库中获取符合条件的专家
 * 1.1.1.2.1 抽到了专家，插入到专家等候队列
 * 1.1.1.2.2 没有抽到专家，说明没有符合条件的专家，更新抽取单的状态为抽取不成功，缺失专家
 * 1.1.2 如果有，说明这个单据的专家已经抽取出来了，在等候打电话
 * 1。2如果有抽取单，但找不到符合条件的专家，则更新抽取单状态，提示失败
 * 2。检查是否有暂停单据
 * 2.1 如果有暂停的单据，这个单据在专家等候队列里有专家等候，则取消等候的专家
 *  * 
 * @author Administrator
 *
 */
public class Spider implements Runnable {
  private static Logger logger = Logger.getLogger(Spider.class);

  private ExpertQueue queue;
  
  public Spider(ExpertQueue queue){
    this.queue=queue;
  }
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    while(true){
      //获取抽取单
      List<Map<String, String>> billLst=scan();
      if(billLst==null || billLst.size()==0){
        logger.info("没有抽取单");
      }else{
        try{
        for(int i=0;i<billLst.size();i++){
          Map<String, String> billMap=billLst.get(i);
          if(IExpertCallConstant.BILL_STATUS_SELECTING.equals(billMap.get("EM_STATUS"))){//抽取中的单据
            //获取对应的专家
            List<ExpertCallInfo> expertLst=getExperts(billMap.get("EM_BILL_CODE"));
            if(expertLst==null || expertLst.size()==0){
              //更新抽取单状态
              updateBillStatus(billMap.get("EM_BILL_CODE"));
            }else{
              //添加到等候列表中
              for (ExpertCallInfo expertCallInfo : expertLst) {
                queue.push(expertCallInfo);
              }
            }
          }else if(IExpertCallConstant.BILL_STATUS_PAUSED.equals(billMap.get("EM_STATUS"))){//暂停的单据
            queue.removeWaitingExperts(billMap.get("EM_BILL_CODE"));
          }
        }
        }catch(EmCallException ex){
          
        }
      }
      try {
        Thread.sleep(10000);//间隔10秒再检索
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block  
        logger.error("检索抽取单异常！"+e.getMessage(),e);
      }
    }
  }

  /**
   * 更新抽取单的状态
   * 如果是暂停状态,则不更新
   * 如果是抽取状态，则查询单据的专家抽取情况和拨打情况，将单据状态更新为 抽取完成或者专家不够
   * @param billCode
   */
  private void updateBillStatus(String billCode) {}
  /**
   * 根据单据号获取需要的专家
   * @param billCode
   * @return
   * @throws EmCallException 
   */
  private List<ExpertCallInfo> getExperts(String billCode) throws EmCallException {
    // EM_RESPONSE_STATUS,响应情况：9-参加
    // 8-不参加
    Object[] params = new Object[] { billCode, "9", billCode };// 获取当前抽取单还需要抽取的专家数量，不包括已经同意来的专家
    DAOFactory df=new DAOFactory();
    Map<String, String> expertNumMap = df.queryToColumnMap(IExpertCallConstant.GET_EXPERT_NUM_FOR_SELECTION, params);
    // 获取当前单据需要抽取专家的数量
    int needExpertNum = Integer.parseInt(expertNumMap.get("NUM"));
    List failTypeLst = new ArrayList();
    List selectedExpertLst = new ArrayList<String>();
    if (needExpertNum > 0) {
      params = new Object[] { billCode };
      // 当前单据的专家类别、抽取数量、呼叫信息、短信信息
      List<Map<String, String>> ecList = df.queryToListMap(IExpertCallConstant.GET_EVALUATION_CONDITION_LIST, params);
      // 专家数量不够的类别
      for (Map<String, String> mcMap : ecList) {
        String emExpertTypeCode = mcMap.get("EM_EXPERT_TYPE_CODE");
        int needExpertNumWithType = Integer.parseInt(mcMap.get("EXPERT_NUM") == null ? "0" : mcMap.get("EXPERT_NUM"));// 对应专家类别所需的专家数据

        String emCallInfo = mcMap.get("EM_CALL_INFO");
        String emMsgInfo = mcMap.get("EM_MSG_INFO");
        params = new Object[] { billCode, emExpertTypeCode };
        Map<String, String> senMap = df.queryToColumnMap(IExpertCallConstant.GET_SELECTED_EXPERT_NUM, params);//已经抽取的专家,只包括同意参加的专家

        int selectedExpertNumWithType = Integer.parseInt(senMap.get("NUM") == null ? "0" : senMap.get("NUM"));// 这个类别已经选择到的专家
        logger.info("=============needExpertNumWithType="+needExpertNumWithType);
        logger.info("=============selectedExpertNumWithType="+selectedExpertNumWithType);
        if (needExpertNumWithType > selectedExpertNumWithType) {
          params = new Object[] { billCode, billCode, billCode, billCode, emExpertTypeCode, emExpertTypeCode };
          List<Map<String, String>> expertList = df.queryToListMap(IExpertCallConstant.GET_EXPERT_LIST, params);
          //
          if (expertList == null && expertList.size() == 0) {// 没有搜到专家，说明专家都抽取完了，还没有找到专家
            failTypeLst.add(emExpertTypeCode);
          } else {
            int j = 0;
            for (int i = 0; i < expertList.size(); i++) {
              String expertCode = expertList.get(i).get("EM_EXPERT_CODE");
              String emMobile = expertList.get(i).get("EM_MOBILE");

              if (selectedExpertLst.contains(expertCode)) {
                continue;
              }

              // params = new Object[] { emBillCode, expertCode,
              // emExpertTypeCode, "0" };
              // df.executeUpdate(conn,
              // INSERT_EM_EXPERT_EVALUATION,
              // params);//插入选择到的专家，其通知状态为0,等待拨打电话

             /* params = new Object[] { expertCode, emMobile, 0, emBillCode, emCallInfo, emMsgInfo, emExpertTypeCode };
              df.executeUpdate(INSERT_EM_CALL_SERVER_LIST, params);// 插入选择到的专家到呼叫表中，其iscall是0，即还未拨打
              logger.info("============="+INSERT_EM_CALL_SERVER_LIST);
              StringBuffer sb=new StringBuffer("=============params=");
              sb.append(expertCode).append(",").append(emBillCode).append(",0,").append(emBillCode).append(",").append(emCallInfo).append(",").append(emMsgInfo).append(",").append(emExpertTypeCode);
              
              logger.info(sb.toString());   */           
              j++;
              logger.info("=============j="+j);
              if (j >= needExpertNumWithType - selectedExpertNumWithType) {
                // 获得需要抽取的专家数量=(需要的数量-已经抽取数量)
                break;
              }
            }
            if (j < needExpertNumWithType - selectedExpertNumWithType) {
              failTypeLst.add(emExpertTypeCode);
            }
          }
        }
      }
    } else {
      /*params = new Object[] { EM_BILL_PRO_STATUS_COMPLETE_SELECTION, emBillCode };
      df.executeUpdate(UPDATE_EM_EXPERT_PRO_BILL_STATUS, params);
      params = new Object[] { EM_BILL_SERVER_STATUS_COMPLETE_SELECTION, emBillCode };
      df.executeUpdate(UPDATE_BILL_SERVER_STATUS, params);*/
    }
    
    return null;
  }
  private List<Map<String, String>> scan() {
    Object[] params = new Object[] {};// 获取等待抽取和暂停中的抽取单
    List<Map<String, String>> scanList=null;
    try {
      DAOFactory df=new DAOFactory();
      scanList = df.queryToListMap(IExpertCallConstant.GET_SELECTING_AND_PAUSED_BILL, params);     
    } catch (EmCallException e) {
//      e.printStackTrace();
      logger.error("扫描专家抽取单异常。\n" + e.getMessage(), e);
    }
    return scanList;
  }

}
