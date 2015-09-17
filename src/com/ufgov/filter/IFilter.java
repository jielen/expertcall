package com.ufgov.filter;

import java.sql.Connection;

/**
 * 过滤实现类基类added by mengw 20100813
 * */
public interface IFilter {

  public boolean dofilter(String emExpertCode, String billCode, String emCatalogueCode, String emYear,
    Connection conn) throws Exception;

}
