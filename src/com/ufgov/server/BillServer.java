package com.ufgov.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ufgov.util.ApplicationContext;
import com.ufgov.util.DAOFactory;
import com.ufgov.util.EmCallException;

/**
 * 负责从接收专家抽取单，收到后，再随机抽取出专家放入待呼叫表中，供呼叫线程进行呼叫。
 * @author Administrator
 */
public class BillServer extends Thread {
  private static Logger logger = Logger.getLogger(BillServer.class);

  private static final String EM_BILL_PRO_STATUS_COMPLETE_SELECTION = "SELECT_FINISH";

  private static final String EM_BILL_PRO_STATUS_COMPLETE_FAIL = "SELECT_FAIL";

  private static final int EM_BILL_SERVER_STATUS_COMPLETE_SELECTION = 4;

  private static final int EM_BILL_SERVER_STATUS_COMPLETE_FAIL = 8;

  // 获得需要抽取的抽取单
  private static final String GET_BILL_SERVER_LIST_BY_STATUS = "SELECT * FROM EM_BILL_SERVER_LIST WHERE EM_STATUS = ?";

  // 获得需要抽取的抽取单,支持多部语音卡连接同一个库，各搜索各自的抽取单
  private static final String GET_BILL_SERVER_LIST_BY_STATUS_2 = "SELECT l.* FROM EM_BILL_SERVER_LIST l,ZC_EM_EXPERT_PRO_BILL b " +
                              "where l.em_bill_code=b.em_bill_code and b.em_co_code=? and l.EM_STATUS = ?";

  // 更新抽取记录的状态
  private static final String UPDATE_BILL_SERVER_STATUS = "UPDATE EM_BILL_SERVER_LIST SET EM_STATUS = ? WHERE EM_BILL_CODE = ?";

  // 更新专家抽取单的状态
  private static final String UPDATE_EM_EXPERT_PRO_BILL_STATUS = "UPDATE ZC_EM_EXPERT_PRO_BILL SET EM_BILL_STATUS = ? WHERE EM_BILL_CODE = ?";

  // private static final String sqlUpdateDone =
  // "update em_bill_server_list set ISCALL=? where em_bill_code=?";

  // private static final String sqlInsertCall =
  // "insert into EM_CALL_SERVER_LIST(OBJID,EM_EXPERT_CODE,EM_MOBILE,EM_CALL_MSG,ISCALL,EM_BILL_CODE,EM_PHONE_MSG)"
  // +
  // "values((select nvl(max(objid),0)+1 from EM_CALL_SERVER_LIST),?,?,?,?,?,?)";

  // 获取当前单据上对应类别的专家，已经打过电话的、过滤的专家不在选择范围内 专家列表 em_type_code like 'xxxx%'
  // 支持选取大类抽取
 /* private static final String GET_EXPERT_LIST = "SELECT * FROM ( SELECT * FROM ZC_EM_B_EXPERT WHERE EM_EXPERT_CODE NOT IN (SELECT EM_EXPERT_CODE  FROM EM_EXPERT_BILL_FILTER WHERE EM_BILL_CODE = ?) "
    + "AND (EM_UNIT_NAME is null or EM_UNIT_NAME NOT IN (SELECT UNIT_NAME FROM EM_EXPERT_BILL_FILTER_UNIT WHERE EM_BILL_CODE =?) )"
    + "AND EM_EXPERT_CODE NOT IN (SELECT EM_EXPERT_CODE  FROM ZC_EM_EXPERT_EVALUATION  WHERE EM_BILL_CODE = ?) "
    + "AND EM_EXPERT_CODE NOT IN (SELECT L.EM_EXPERT_CODE FROM EM_CALL_SERVER_LIST L  WHERE L.EM_BILL_CODE=? AND L.EM_EXPERT_TYPE_CODE=?) "
    + "AND EM_EXPERT_CODE IN (SELECT EM_EXPERT_CODE FROM ZC_Em_Expert_Type_Join WHERE em_type_code like ?||'%') AND EM_EXP_STATUS='enable' ORDER BY dbms_random.VALUE ) WHERE rownum < 100";
*/
  private static final String GET_EXPERT_LIST = "SELECT * FROM ("
        +" SELECT * FROM ZC_EM_B_EXPERT    WHERE EM_EXPERT_CODE NOT IN"
            +" (SELECT EM_EXPERT_CODE FROM EM_EXPERT_BILL_FILTER WHERE EM_BILL_CODE = ?"
              +" union "
              +" SELECT EM_EXPERT_CODE  FROM ZC_EM_EXPERT_EVALUATION  WHERE EM_BILL_CODE = ?"
              +" union"
              +" SELECT L.EM_EXPERT_CODE  FROM EM_CALL_SERVER_LIST L  WHERE L.EM_BILL_CODE = ?"
           //     +" AND L.EM_EXPERT_TYPE_CODE = ?"
            /*  +" union"
              +" SELECT L.EM_EXPERT_CODE FROM EM_CALL_SERVER_LIST L"
                 +" WHERE L.EM_BILL_CODE != ? and l.iscall='0'"*/
              +" union "
              +" select e.em_expert_code"
                +" from ZC_EM_EXPERT_EVALUATION e, ZC_EM_EXPERT_PRO_BILL pb,ZC_EM_EXPERT_PRO_BILL b"
                +" where e.em_bill_code = pb.em_bill_code  and e.em_response_status = '9'"
                +" and e.em_bill_code !=  b.em_bill_code  and b.em_bill_code=? "
                +" and TRUNC(b.em_tenders_time) - TRUNC(pb.em_tenders_time) = 0  "
            +" )"
            +" AND ( EM_UNIT_NAME is null or  EM_UNIT_NAME NOT IN"
                    +" (SELECT UNIT_NAME  FROM EM_EXPERT_BILL_FILTER_UNIT  WHERE EM_BILL_CODE = ?)"
                +" ) "
            +" AND EM_EXPERT_CODE IN (SELECT EM_EXPERT_CODE  FROM ZC_Em_Expert_Type_Join"
                                   +"  WHERE em_type_code like ? || '%')"
                                   +" AND EM_EXP_STATUS = 'enable'"
            +" ORDER BY dbms_random.VALUE)"
         +" WHERE rownum < 100";
  // 当前单据的专家类别、抽取数量、呼叫信息、短信信息
  private static final String GET_EVALUATION_CONDITION_LIST = "SELECT EC.EM_EXPERT_TYPE_CODE,EC.EXPERT_NUM,B.EM_CALL_INFO,B.EM_MSG_INFO FROM EM_EVALUATION_CONDITION EC, ZC_EM_EXPERT_PRO_BILL B WHERE EC.EM_BILL_CODE = B.EM_BILL_CODE AND B.EM_BILL_CODE = ?";

  // 获取当前抽取单还需要抽取的专家数量
  private static final String GET_EXPERT_NUM_FOR_SELECTION = "SELECT (B.NUM - A.NUM) NUM FROM (SELECT NVL(COUNT(*), 0) NUM FROM ZC_EM_EXPERT_EVALUATION WHERE EM_BILL_CODE = ? AND EM_RESPONSE_STATUS = ?) A, (SELECT NVL(SUM(EC.EXPERT_NUM), 0) NUM FROM EM_EVALUATION_CONDITION EC WHERE EC.EM_BILL_CODE = ?) B";

  // 已经抽取的专家,包括同意参加的专家
  private static final String GET_SELECTED_EXPERT_NUM = "SELECT NVL(COUNT(EM_EXPERT_CODE), 0) NUM FROM ZC_EM_EXPERT_EVALUATION WHERE EM_BILL_CODE = ? AND EM_EXPERT_TYPE_CODE = ? AND EM_RESPONSE_STATUS='9'";

  // 插入选择到的专家，其他通知状态为0,等待拨打电话
  // private static final String INSERT_EM_EXPERT_EVALUATION =
  // "INSERT INTO ZC_EM_EXPERT_EVALUATION (EM_BILL_CODE, EM_EXPERT_CODE, EM_EXPERT_TYPE_CODE, EM_NOTICE_STATUS) VALUES (?, ?, ?, ?)";

  // 插入待拨打电话的专家记录，其iscall是0，即还未拨打
  private static final String INSERT_EM_CALL_SERVER_LIST = "INSERT INTO EM_CALL_SERVER_LIST (OBJID, EM_EXPERT_CODE, EM_MOBILE, ISCALL, EM_BILL_CODE, EM_CALL_MSG, EM_PHONE_MSG,em_expert_type_code) VALUES ((SELECT NVL(MAX(OBJID), 0) + 1 FROM EM_CALL_SERVER_LIST),?,?,?,?,?,?,?)";

  private static final String GET_CALLING_EXPERT_NUM = "SELECT COUNT(*) CALLING_NUM FROM EM_CALL_SERVER_LIST L WHERE L.ISCALL <>'-1' AND L.EM_BILL_CODE =? GROUP BY EM_BILL_CODE ";

  public void run() {
    int scanIntervalTime = 10000;
    String scanIntervalTimeStr = ApplicationContext.singleton().getValueAsString("scanIntervalTime");
    if (scanIntervalTimeStr != null) {
      scanIntervalTime = Integer.parseInt(scanIntervalTimeStr);
    }
    while (true) {
      try {
        scan();
//        logger.info("扫描间隔 10s");
        Thread.sleep(scanIntervalTime);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        logger.error("扫描专家服务线程异常。\n" + e.getMessage(), e);
      }
    }
  }

  private void scan() {
    Object[] params = new Object[] { new Integer(0) };// 获取等待抽取的抽取单
    Object[] params2 = new Object[] {ServiceContext.phonecard,new Integer(0) };// 获取等待抽取的抽取单
    List<Map<String, String>> scanList=null;
    try {
      DAOFactory df=new DAOFactory();
      logger.info("获取等待抽取的抽取单");
      if(ServiceContext.isMutilPhoneCard()){
        scanList = df.queryToListMap(GET_BILL_SERVER_LIST_BY_STATUS_2, params2);  
        logger.debug(GET_BILL_SERVER_LIST_BY_STATUS_2);
        logger.debug(ServiceContext.phonecard);
        logger.debug(0);
      }else{
        scanList = df.queryToListMap(GET_BILL_SERVER_LIST_BY_STATUS, params); 
        logger.debug(GET_BILL_SERVER_LIST_BY_STATUS);
        logger.debug(0);
      }
      if (scanList == null || scanList.size() < 1) {
        logger.info("当前无抽取单。");
        return;
      }
      for (Map<String, String> m : scanList) {
        String emBillCode = m.get("EM_BILL_CODE");
        if (existCallingRecord(emBillCode)) {// 正在拨打电话的单据不进行专家抽取，打完电话了，在进行数据检查，看是否还是否还要在抽取
          continue;
        }
        selectExperts(emBillCode);
      }
    } catch (EmCallException e) {
      e.printStackTrace();
      logger.error("扫描专家服务异常。\n" + e.getMessage(), e);
    }
  }

  /**
   * 是否存在等待呼叫记录
   * @param emBillCode
   * @return
   */
  private boolean existCallingRecord(String emBillCode) throws EmCallException {

    logger.debug("正在拨打电话的单据不进行专家抽取，打完电话了，在进行数据检查，看是否还是否还要在抽取");
    Object[] params = new Object[] {emBillCode };
    String sql = "SELECT COUNT(*) AS SUM FROM EM_CALL_SERVER_LIST C WHERE C.ISCALL =0  AND C.EM_BILL_CODE=?";
    logger.debug("是否存在等待呼叫记录");
    logger.debug(""+sql);
    logger.debug(""+emBillCode);
    DAOFactory df=new DAOFactory();
    Map<String, String> billMap = df.queryToColumnMap(sql, params);
    if (billMap != null) {
      String sumStr = billMap.get("SUM");

      logger.debug("sumStr="+sumStr);
      
      int sum = Integer.parseInt(sumStr == null ? "0" : sumStr);
      if (sum > 0) { return true; }
    }
    return false;
  }

  /**
   * 是否正在抽取的抽取单，返回抽取单的状态
   * @param emBillCode
   * @return
   * @throws EmCallException
   */
  /*
   * private boolean isSelectingBill(String emBillCode) throws
   * EmCallException{
   * 
   * Object[] params = new Object[] { emBillCode }; String sql=
   * "SELECT B.EM_BILL_STATUS FROM ZC_EM_EXPERT_PRO_BILL  B WHERE B.EM_BILL_CODE=?"
   * ; Map<String, String> billMap = df.queryToColumnMap(sql, params);
   * if(billMap!=null){ String billStatus=billMap.get("EM_BILL_STATUS");
   * if("SELECTING".equals(billStatus)){ return true; } } return false; }
   */

  private void selectExperts(String emBillCode) throws EmCallException {
    Object[] params = new Object[] { emBillCode, "9", emBillCode };// 获取当前抽取单还需要抽取的专家数量，不包括已经同意来的专家
    // EM_RESPONSE_STATUS,响应情况：9-参加
    // 8-不参加
    DAOFactory df=new DAOFactory();
    Map<String, String> expertNumMap = df.queryToColumnMap(GET_EXPERT_NUM_FOR_SELECTION, params);
    logger.debug("获取当前抽取单还需要抽取的专家数量");
    logger.debug(""+GET_EXPERT_NUM_FOR_SELECTION);
    logger.debug(""+emBillCode+",9,"+emBillCode);
    // 获取当前单据需要抽取专家的数量
    int needExpertNum = Integer.parseInt(expertNumMap.get("NUM"));
    List failTypeLst = new ArrayList();
    List selectedExpertLst = new ArrayList<String>();
    if (needExpertNum > 0) {
      params = new Object[] { emBillCode };
      // 当前单据的专家类别、抽取数量、呼叫信息、短信信息
      List<Map<String, String>> ecList = df.queryToListMap(GET_EVALUATION_CONDITION_LIST, params);
      logger.debug("获取当前单据的专家类别、抽取数量、呼叫信息、短信信息");
      logger.debug(""+GET_EVALUATION_CONDITION_LIST);
      logger.debug(""+emBillCode);
      // 专家数量不够的类别
      for (Map<String, String> mcMap : ecList) {
        String emExpertTypeCode = mcMap.get("EM_EXPERT_TYPE_CODE");
        int needExpertNumWithType = Integer.parseInt(mcMap.get("EXPERT_NUM") == null ? "0" : mcMap.get("EXPERT_NUM"));// 对应专家类别所需的专家数据

        String emCallInfo = mcMap.get("EM_CALL_INFO");
        String emMsgInfo = mcMap.get("EM_MSG_INFO");
        params = new Object[] { emBillCode, emExpertTypeCode };
        Map<String, String> senMap = df.queryToColumnMap(GET_SELECTED_EXPERT_NUM, params);
        logger.debug("已经抽取的专家,包括同意参加的专家");
        logger.debug(GET_SELECTED_EXPERT_NUM);
        logger.debug(emBillCode+","+emExpertTypeCode);

        int selectedExpertNumWithType = Integer.parseInt(senMap.get("NUM") == null ? "0" : senMap.get("NUM"));// 这个类别已经选择到的专家
        logger.debug("needExpertNumWithType="+needExpertNumWithType);
        logger.debug("selectedExpertNumWithType="+selectedExpertNumWithType);
        if (needExpertNumWithType > selectedExpertNumWithType) {
          params = new Object[] { emBillCode, emBillCode, emBillCode,emBillCode, emBillCode, emExpertTypeCode};
          List<Map<String, String>> expertList = df.queryToListMap(GET_EXPERT_LIST, params);
          logger.debug("获取当前单据上对应类别的专家，已经打过电话的、过滤的专家不在选择范围内 ");
          logger.debug(GET_EXPERT_LIST);
          logger.debug(emBillCode+","+emExpertTypeCode);
          //
          if (expertList == null && expertList.size() == 0) {// 没有搜到专家，说明专家都抽取完了，还没有找到专家
            logger.debug("没有搜到专家，说明专家都抽取完了，还没有找到专家,emExpertTypeCode="+emExpertTypeCode);
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

              params = new Object[] { expertCode, emMobile, 0, emBillCode, emCallInfo, emMsgInfo, emExpertTypeCode };
              df.executeUpdate(INSERT_EM_CALL_SERVER_LIST, params);// 插入选择到的专家到呼叫表中，其iscall是0，即还未拨打
              logger.debug("插入选择到的专家到呼叫表中，其iscall是0，即还未拨打");
              logger.debug(INSERT_EM_CALL_SERVER_LIST);
              StringBuffer sb=new StringBuffer("");
              sb.append(expertCode).append(",").append(emBillCode).append(",0,").append(emBillCode).append(",").append(emCallInfo).append(",").append(emMsgInfo).append(",").append(emExpertTypeCode);
              logger.debug(sb.toString());              
              j++;
//              logger.info("===j="+j);
              if (j >= needExpertNumWithType - selectedExpertNumWithType) {
                // 获得需要抽取的专家数量=(需要的数量-已经抽取数量)
                break;
              }
            }
            if (j < needExpertNumWithType - selectedExpertNumWithType) {
              StringBuffer sb =new StringBuffer();
              sb.append("专家类别:").append(emExpertTypeCode).append(",需要专家:").append(needExpertNumWithType).append(",已经抽取到数量:").append(selectedExpertNumWithType).append(",本次轮询查到的数量:").append(j);
              sb.append(",抽到专家数量少于要求数量，抽取失败");
              logger.info(sb);
              failTypeLst.add(emExpertTypeCode);
            }
          }
        }
      }
    } else {

      logger.debug("抽取完成");
      params = new Object[] { EM_BILL_PRO_STATUS_COMPLETE_SELECTION, emBillCode };
      df.executeUpdate(UPDATE_EM_EXPERT_PRO_BILL_STATUS, params);
      logger.debug(UPDATE_EM_EXPERT_PRO_BILL_STATUS);
      logger.debug(EM_BILL_PRO_STATUS_COMPLETE_SELECTION+","+emBillCode);
      params = new Object[] { EM_BILL_SERVER_STATUS_COMPLETE_SELECTION, emBillCode };
      df.executeUpdate(UPDATE_BILL_SERVER_STATUS, params);
      logger.debug(UPDATE_BILL_SERVER_STATUS);
      logger.debug(EM_BILL_SERVER_STATUS_COMPLETE_SELECTION+","+emBillCode);
    }

    // 如果抽取到的专家不够，且电话都打完了，则更新当前抽取单的状态为抽取失败
    if (failTypeLst.size() > 0 && !existCallingRecord(emBillCode)) {
      logger.debug("如果抽取到的专家不够，且电话都打完了，则更新当前抽取单的状态为抽取失败");
      params = new Object[] { EM_BILL_PRO_STATUS_COMPLETE_FAIL, emBillCode };
      df.executeUpdate(UPDATE_EM_EXPERT_PRO_BILL_STATUS, params);
      logger.debug(UPDATE_EM_EXPERT_PRO_BILL_STATUS);
      logger.debug(EM_BILL_PRO_STATUS_COMPLETE_FAIL+","+emBillCode);
      params = new Object[] { EM_BILL_SERVER_STATUS_COMPLETE_FAIL, emBillCode };
      df.executeUpdate(UPDATE_BILL_SERVER_STATUS, params);
      logger.debug(UPDATE_BILL_SERVER_STATUS);
      logger.debug(EM_BILL_SERVER_STATUS_COMPLETE_FAIL+","+emBillCode);
    }

  }

}
