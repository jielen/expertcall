package com.ufgov.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 过滤掉前后一天内已经参加了评审 added by mengw 20100815
 * */
public class TimeFilter implements IFilter {
  private final static int TIME_LEN=1;

  public boolean dofilter(String emExpertCode, String billCode, String emCatalogueCode, String emYear,
    Connection conn) throws Exception {

    int em_Tenders_Time;
    PreparedStatement pst = conn
      .prepareStatement("select EM_TENDERS_TIME -(select EM_TENDERS_TIME from  em_expert_pro_bill   where  EM_BILL_CODE ='"
        + billCode
        + "')from  em_expert_pro_bill a,em_expert_evaluation b  where a.EM_BILL_CODE = b.EM_BILL_CODE and b.em_Expert_Code='"
        + emExpertCode + "'");
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {

      em_Tenders_Time = rs.getInt("EM_TENDERS_TIME");
      if (-1 < em_Tenders_Time && em_Tenders_Time < TimeFilter.TIME_LEN && em_Tenders_Time != 0)
        return true;

    }
    rs.close();
    pst.close();

    return false;
  }

}
