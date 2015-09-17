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

	private static final Logger logger=Logger.getLogger(DAOFactory.class);
	
  private static final String HOST_NAME = ApplicationContext.singleton().getValueAsString("hostname");

  private static final String PORT = ApplicationContext.singleton().getValueAsString("port");

  private static final String SID = ApplicationContext.singleton().getValueAsString("sid");

  private static final String USER_NAME = ApplicationContext.singleton().getValueAsString("username");

  private static final String PASSWORD = ApplicationContext.singleton().getValueAsString("password");

  private static final String URI = "jdbc:oracle:thin:@${HOST_NAME}:${PORT}:${SID}".replace("${HOST_NAME}",
    HOST_NAME).replace("${PORT}", PORT).replace("${SID}", SID);
  
  private static DAOFactory DAOFactory = null;
  
  public DAOFactory() {
  }


  @SuppressWarnings("unchecked")
  public static void setStatementParameters(PreparedStatement stmt, Object[] params) throws SQLException {
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

  
  public static synchronized DAOFactory getInstance() {
	    if (DAOFactory == null)
	      DAOFactory = new DAOFactory();
	    return DAOFactory;
  }
  


  public static List<Map<String, String>> getResultList(ResultSet rs) throws SQLException {
    List<Map<String, String>> result = new ArrayList<Map<String, String>>();
    String[] column = getColumn(rs);
    Map<String, String> row;
    String value;
    while (rs.next()) {
      row = new HashMap<String, String>();
      for (int i = 0, j = column.length; i < j; i++) {
        value = rs.getString(i + 1);
        if (value == null)
          value = "";
        row.put(column[i], value);
      }
      result.add(row);
    }
    return result;
  }

  private static String[] getColumn(ResultSet rs) throws SQLException {
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    String[] column = new String[columnCount];
    for (int i = 0; i < columnCount; i++) {
      column[i] = rsmd.getColumnName(i + 1);
    }
    return column;
  }
  
  private static List<Connection> pools=new ArrayList<Connection>();
  
  private static int poolSize=5;
  
  public static synchronized Connection getConnection() throws SQLException {
	  if(pools.size()==0){
			 try {
			     Class.forName("oracle.jdbc.driver.OracleDriver");
			 } catch (ClassNotFoundException e) {
			     e.printStackTrace();
			     throw new RuntimeException(e);
			 }
			  for(int i=0;i<poolSize;i++){
				  Connection conn = DriverManager.getConnection(URI, USER_NAME, PASSWORD);
				  pools.add(conn);
			  } 
	  }
	  if(pools.size()>0){
		  Connection con=pools.get(pools.size()-1);
		  pools.remove(pools.size()-1);
		  return con;
	  }else{
		  throw new SQLException("无法获取数据库连接");
	  }
  }
  
  public static void closeConnection(Connection conn, Statement stmt, ResultSet rs) {
    try {
      if (null != rs) {
        rs.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    try {
      if (null != stmt) {
        stmt.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    
      if (null != conn ) {
        pools.add(conn);
      }
  }


  public static int executeUpdate(Connection conn, String sql, Object[] params) throws SQLException {
    PreparedStatement stmt = null;
    try {
      stmt = conn.prepareStatement(sql);     
      if (null != params && params.length > 0) {
        setStatementParameters(stmt, params);
      }
      return stmt.executeUpdate();
    } finally {
      closeConnection(null, stmt, null);
    }
  }

  public static List<Map<String, String>> queryToListMap(String sql, Object[] params) throws SQLException {
	  
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      stmt = conn.prepareStatement(sql);
      if (null != params && params.length > 0) {
        setStatementParameters(stmt, params);		
      }
      rs = stmt.executeQuery();
      return getResultList(rs);
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      closeConnection(conn, stmt, rs);
    }
  }

  public static Map<String, String> queryToColumnMap(String sql, Object[] params) throws SQLException {
    List<Map<String, String>> list = queryToListMap(sql, params);
    return list.size() > 0 ? list.get(0) : null;
  }
}
