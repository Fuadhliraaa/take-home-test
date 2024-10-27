package com.assignment.test.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Map;

public class PreparedStatementHelper {
  
  private static final Logger log = LoggerFactory.getLogger(PreparedStatementHelper.class);
  
  private static Connection con;
  private static PreparedStatement ps;
  private static ResultSet rs;
  
  //  TRANSACTION TABLE
  public static void saveTransaction(String JDBC_URL, String USERNAME, String PASSWORD, String sqlQuery, Map<Object, Object> mapValue) {
    try {
      con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
      ps = con.prepareCall(sqlQuery);
      ps.setString(1, mapValue.get("trxId").toString());
      ps.setString(2, mapValue.get("email").toString());
      ps.setString(3, mapValue.get("invoiceNo").toString());
      ps.setString(4, mapValue.get("serviceCode").toString());
      ps.setString(5, mapValue.get("serviceName").toString());
      ps.setString(6, mapValue.get("trxType").toString());
      ps.setBigDecimal(7, new BigDecimal(String.valueOf(mapValue.get("amount"))));
      ps.setTimestamp(8, Timestamp.valueOf(mapValue.get("timestamp").toString()));
      ps.setString(9, mapValue.get("desc").toString());
      
      ps.executeUpdate();
      
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        throw new RuntimeException();
      }
    }
    
    
  }
  
  public static void updateUserBalance(
      String JDBC_URL,
      String USERNAME,
      String PASSWORD,
      String sql,
      Map<String, Object> mapVal) {
    
    try {
      
      con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
      ps = con.prepareCall(sql);
      ps.setBigDecimal(1, new BigDecimal(String.valueOf(mapVal.get("balance"))));
      ps.setString(2, mapVal.get("email").toString());
      
      ps.executeUpdate();
      
    } catch (SQLException e) {
      throw new RuntimeException();
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        throw new RuntimeException();
      }
    }
    
  }
  
  //  USERS TABLE
  public static void saveUser(
      String JDBC_URL,
      String USERNAME,
      String PASS,
      String sql,
      Map<String, Object> valMap) {
    
    try {
      
      con = DriverManager.getConnection(JDBC_URL, USERNAME, PASS);
      ps = con.prepareCall(sql);
      ps.setString(1, valMap.get("uuid").toString());
      ps.setString(2, valMap.get("email").toString());
      ps.setString(3, valMap.get("firstName").toString());
      ps.setString(4, valMap.get("lastName").toString());
      ps.setString(5, valMap.get("password").toString());
      
      ps.executeUpdate();
      
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        throw new RuntimeException();
      }
    }
    
  }
  
  public static void updateUserProfile(
      String JDBC_URL,
      String USERNAME,
      String PASSWORD,
      String sql,
      Map<String, Object> mapVal) {
    
    try {
      
      con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
      ps = con.prepareCall(sql);
      ps.setString(1, mapVal.get("firstName").toString());
      ps.setString(2, mapVal.get("lastName").toString());
      ps.setString(3, mapVal.get("email").toString());
      
      ps.executeUpdate();
      
    } catch (SQLException e) {
      throw new RuntimeException();
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        throw new RuntimeException();
      }
    }
    
  }
  
  
  //  USER PIC TABLE
  public static void saveUserPic(
      String JDBC_URL,
      String USERNAME,
      String PASS,
      String sql,
      Map<String, Object> valMap) {
    
    try {
      
      con = DriverManager.getConnection(JDBC_URL, USERNAME, PASS);
      ps = con.prepareCall(sql);
      ps.setString(1, valMap.get("uuid").toString());
      ps.setString(2, valMap.get("generatedName").toString());
      ps.setString(3, valMap.get("imageDir").toString());
      ps.setInt(4, Integer.parseInt(valMap.get("size").toString()));
      ps.setString(5, valMap.get("email").toString());
      
      ps.executeUpdate();
      
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        throw new RuntimeException();
      }
    }
    
  }
  
}
