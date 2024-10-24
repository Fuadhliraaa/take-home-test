package com.assignment.test.utils;

import com.assignment.test.constant.QueryConstant;
import com.assignment.test.constant.TrxConstant;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Map;

public class PreparedStatementHelper {
  
  private static Connection con;
  private static PreparedStatement ps;
  private static ResultSet rs;
  
  public static void saveTransaction(String sqlQuery, Map<Object, Object> mapValue) {
    
    try {
      con = DriverManager.getConnection(QueryConstant.JDBC_URL, QueryConstant.USERNAME, QueryConstant.PASSWORD);
      ps = con.prepareCall(sqlQuery);
      ps.setString(1, UserHelper.generateUUID());
      ps.setString(2, mapValue.get("email").toString());
      ps.setString(3, CommonUtils.generateInvoceNo());
      ps.setString(4, mapValue.get("serviceCode").toString());
      ps.setString(5, mapValue.get("serviceName").toString());
      ps.setString(6, TrxConstant.TRX_TYPE_TOPUP);
      ps.setBigDecimal(7, new BigDecimal(String.valueOf(mapValue.get("amount"))));
      ps.setTimestamp(8, CommonUtils.getCurrentTimestamp());
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
  
  public static void updateUserBalance(String sql, Map<Object, Object> mapVal) {
    
    try {
    
    con = DriverManager.getConnection(QueryConstant.JDBC_URL, QueryConstant.USERNAME, QueryConstant.PASSWORD);
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
  
}
