package com.ufgov.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DAOFactory {

  private static final Logger logger = Logger.getLogger(DAOFactory.class);

//  private static final String HOST_NAME = ApplicationContext.singleton().getValueAsString("hostname");
//
//  private static final String PORT = ApplicationContext.singleton().getValueAsString("port");
//
//  private static final String SID = ApplicationContext.singleton().getValueAsString("sid");
//
//  private static final String USER_NAME = ApplicationContext.singleton().getValueAsString("username");
//
//  private static final String PASSWORD = ApplicationContext.singleton().getValueAsString("password");
//
//  private static final String URI = "jdbc:oracle:thin:@${HOST_NAME}:${PORT}:${SID}".replace("${HOST_NAME}", HOST_NAME).replace("${PORT}", PORT).replace("${SID}", SID);
  

  public DAOFactory() {}

  @SuppressWarnings("unchecked")
  public  void setStatementParameters(PreparedStatement stmt, Object[] params) throws SQLException {
    for (int i = 0; i < params.length; ++i) {
      Object o = params[i];
      if (null == o) {
        stmt.setNull(i + 1, Types.CHAR);
      } else if (o instanceof List) {
        List list = (List) o;
        stmt.setBinaryStream(4, (InputStream) list.get(0), (Integer) list.get(1));
      } else {
        stmt.setObject(i + 1, o);
      }
    }
  }

  
  public  List<Map<String, String>> getResultList(ResultSet rs) throws SQLException {
    List<Map<String, String>> result = new ArrayList<Map<String, String>>();
    String[] column = getColumn(rs);
    Map<String, String> row;
    String value;
    while (rs.next()) {
      row = new HashMap<String, String>();
      for (int i = 0, j = column.length; i < j; i++) {
        value = rs.getString(i + 1);
        if (value == null) value = "";
        row.put(column[i], value);
      }
      result.add(row);
    }
    return result;
  }

  private  String[] getColumn(ResultSet rs) throws SQLException {
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    String[] column = new String[columnCount];
    for (int i = 0; i < columnCount; i++) {
      column[i] = rsmd.getColumnName(i + 1);
    }
    return column;
  }

  private  Connection getConnection() throws EmCallException {
   /* if (pools.size() == 0) {
      try {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        for (int i = 0; i < poolSize; i++) {
          Connection conn = DriverManager.getConnection(URI, USER_NAME, PASSWORD);
          pools.add(conn);
        }
      } catch (ClassNotFoundException e) {
        //			     e.printStackTrace();
        throw new EmCallException("无法加载数据库驱动：oracle.jdbc.driver.OracleDriver.\n" + e.getMessage(), e);
      } catch (SQLException e) {
        throw new EmCallException("无法建立数据库连接.\n" + e.getMessage(), e);
      }
    }
    if (pools.size() > 0) {
      Connection con = pools.get(pools.size() - 1);
      pools.remove(pools.size() - 1);
      try {
        if (con.isClosed()) {
          return getConnection();
        } else {
          return con;
        }
      } catch (Exception e) {
        throw new EmCallException("无法获取数据库连接。\n" + e.getMessage(), e);
      }
    } else {
      throw new EmCallException("无法获取数据库连接.连接池为空.");
    }*/

    Connection con=null;
    try {
      con = MyConnectionPool.getInstance().getConnection();
    } catch (SQLException e) {
      throw new EmCallException("获取数据库连接异常。\n"+e.getMessage(),e);
    }
    if (con==null) {
        throw new EmCallException("无法获取数据库连接。\n");
    } else {
      return con;
    }
  }

  public  void closeConnection(Connection conn, Statement stmt, ResultSet rs)  {
    _closeResultSet(rs);
    _closeStatement(stmt);
    _closeConnetion(conn);
  }

  private  void _closeConnetion(Connection conn){
    if (null == conn) return;
    /*try {
      if (conn.isClosed()) return;
      if (!conn.getAutoCommit()) {
        conn.setAutoCommit(true);
      }
      if (pools.size() >= poolSize) {
        conn.close();
      } else {
        pools.add(conn);
      }
    } catch (SQLException e) {
      throw new SQLException("关闭Connection异常.\n" + e.getMessage(), e);
    }*/
    MyConnectionPool.getInstance().returnConnection(conn);
  }

  private  void _closeStatement(Statement stmt)  {
    try {
      if (null != stmt) {
        stmt.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error("关闭Statement异常\n"+e.getMessage(),e);    
    }
  }

  private  void _closeResultSet(ResultSet rs)  {
    try {
      if (null != rs) {
        rs.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error("关闭ResultSet异常\n"+e.getMessage(),e);   
    }
  }
 
  public  int executeUpdate(String sql, Object[] params) throws EmCallException {
    PreparedStatement stmt = null;
    Connection conn = null;
    try {
      conn = getConnection();
      stmt = conn.prepareStatement(sql);
      if (null != params && params.length > 0) {
        setStatementParameters(stmt, params);
      }
      return stmt.executeUpdate();
    } catch (SQLException e) {
      StringBuffer sb = new StringBuffer();
      sb.append("更新异常.\n");
      sb.append("sql:【").append(sql).append("】\n");
      if (params != null) {
        sb.append("sql参数数量=").append(params.length).append("\n").append("具体参数:");
        for (int i = 0; i < params.length; i++) {
          if (i == 0) {
            sb.append("【").append(params[i]).append("】");
          } else {
            sb.append(",【").append(params[i]).append("】");
          }
        }
      } else {
        sb.append("参数为 null.\n");
      }
      sb.append(e.getMessage());
      throw new EmCallException(sb.toString(), e);
    } finally {
      closeConnection(conn, stmt, null);
    }
  }

  public  List<Map<String, String>> queryToListMap(String sql, Object[] params) throws EmCallException {

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<Map<String, String>> rtn=new ArrayList<Map<String,String>>();
    try {
      conn = getConnection();
      stmt = conn.prepareStatement(sql);
      if (null != params && params.length > 0) {
        setStatementParameters(stmt, params);
      }
      rs = stmt.executeQuery();
      rtn= getResultList(rs);
    } catch (SQLException e) {
      StringBuffer sb = new StringBuffer();
      sb.append("查询异常.\n");
      sb.append("sql:【").append(sql).append("】\n");
      if (params != null) {
        sb.append("sql参数数量=").append(params.length).append("\n").append("具体参数:");
        for (int i = 0; i < params.length; i++) {
          if (i == 0) {
            sb.append("【").append(params[i]).append("】");
          } else {
            sb.append(",【").append(params[i]).append("】");
          }
        }
      } else {
        sb.append("参数为 null.\n");
      }
      sb.append(e.getMessage());
      throw new EmCallException(sb.toString(), e);
    } catch (EmCallException e) {
      throw e;
    } finally {
      closeConnection(conn, stmt, rs);
    }
    return rtn;
  }

  public  Map<String, String> queryToColumnMap(String sql, Object[] params) throws EmCallException {
    List<Map<String, String>> list = queryToListMap(sql, params);
    return list.size() > 0 ? list.get(0) : null;
  }
}
