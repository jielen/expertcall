/**
 * 
 */
package com.ufgov.second;

/**
 * 专家抽取常量类
 * @author Administrator
 *
 */
public interface IExpertCallConstant {
  /**
   * 抽取单状态：抽取中
   */
  public static final String BILL_STATUS_SELECTING="0";
  /**
   * 抽取单状态：抽取成功
   */
  public static final String BILL_STATUS_SUCCESS="4";
  /**
   * 抽取单状态：专家不够
   */
  public static final String BILL_STATUS_NO_ENOUGH_EXPERTS="8";
  /**
   * 抽取单状态:暂停
   */
  public static final String BILL_STATUS_PAUSED="6";
  
  // 获得需要抽取的抽取单
  public static final String GET_BILL_SERVER_LIST_BY_STATUS = "SELECT * FROM EM_BILL_SERVER_LIST WHERE EM_STATUS = ?";
  
  public static final String GET_SELECTING_AND_PAUSED_BILL = "SELECT * FROM EM_BILL_SERVER_LIST WHERE EM_STATUS IN('0','6')";
  
//获取当前抽取单还需要抽取的专家数量
  public static final String GET_EXPERT_NUM_FOR_SELECTION = "SELECT (B.NUM - A.NUM) NUM FROM (SELECT NVL(COUNT(*), 0) NUM FROM ZC_EM_EXPERT_EVALUATION WHERE EM_BILL_CODE = ? AND EM_RESPONSE_STATUS = ?) A, (SELECT NVL(SUM(EC.EXPERT_NUM), 0) NUM FROM EM_EVALUATION_CONDITION EC WHERE EC.EM_BILL_CODE = ?) B";

  // 当前单据的专家类别、抽取数量、呼叫信息、短信信息
  public static final String GET_EVALUATION_CONDITION_LIST = "SELECT EC.EM_EXPERT_TYPE_CODE,EC.EXPERT_NUM,B.EM_CALL_INFO,B.EM_MSG_INFO FROM EM_EVALUATION_CONDITION EC, ZC_EM_EXPERT_PRO_BILL B WHERE EC.EM_BILL_CODE = B.EM_BILL_CODE AND B.EM_BILL_CODE = ?";


  // 已经抽取的专家,只包括同意参加的专家
  public static final String GET_SELECTED_EXPERT_NUM = "SELECT NVL(COUNT(EM_EXPERT_CODE), 0) NUM FROM ZC_EM_EXPERT_EVALUATION WHERE EM_BILL_CODE = ? AND EM_EXPERT_TYPE_CODE = ? AND EM_RESPONSE_STATUS='9'";

  // 获取当前单据上对应类别的专家，已经打过电话的、过滤的专家不在选择范围内 专家列表 em_type_code like 'xxxx%'
  // 支持选取大类抽取
  public static final String GET_EXPERT_LIST = "SELECT * FROM ( SELECT * FROM ZC_EM_B_EXPERT WHERE EM_EXPERT_CODE NOT IN (SELECT EM_EXPERT_CODE  FROM EM_EXPERT_BILL_FILTER WHERE EM_BILL_CODE = ?) "
    + "AND EM_UNIT_NAME NOT IN (SELECT UNIT_NAME FROM EM_EXPERT_BILL_FILTER_UNIT WHERE EM_BILL_CODE =?) "
    + "AND EM_EXPERT_CODE NOT IN (SELECT EM_EXPERT_CODE  FROM ZC_EM_EXPERT_EVALUATION  WHERE EM_BILL_CODE = ?) "
    + "AND EM_EXPERT_CODE NOT IN (SELECT L.EM_EXPERT_CODE FROM EM_CALL_SERVER_LIST L  WHERE L.EM_BILL_CODE=? AND L.EM_EXPERT_TYPE_CODE=?) "
    + "AND EM_EXPERT_CODE IN (SELECT EM_EXPERT_CODE FROM ZC_Em_Expert_Type_Join WHERE em_type_code like ?||'%') AND EM_EXP_STATUS='enable' ORDER BY dbms_random.VALUE ) WHERE rownum < 100";

}
