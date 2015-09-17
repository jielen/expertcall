package com.ufgov.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 抽取专家过滤器 added by mengw 20100813
 * */
public class Filter {

  public boolean dofilter(String emExpertCode, String billCode, String emCatalogueCode, String emYear,
    Connection conn) {
    //默认返回true，当输入的条件在排除范围内则返回false
    boolean sign = false;
    String filterPath;
    PreparedStatement pst;
    ResultSet rs;
    try {
      pst = conn.prepareStatement("select * from EM_EXPERT_FILTER_PATH");
      rs = pst.executeQuery();
      while (rs.next()) {
        filterPath = rs.getString("filter_path");

        IFilter filter = (IFilter) Class.forName(filterPath).newInstance();
        if (filter.dofilter(emExpertCode, billCode, emCatalogueCode, emYear, conn)) {
          sign = true;
          break;
        }
      }
      rs.close();
      pst.close();
    } catch (Exception e) {
      //过滤产生异常不进行过滤
      return false;
    } finally {
    }
    return sign;
  }

}
