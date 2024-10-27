package com.assignment.test.constant;

public class QueryConstant {

//  TABLE USER
  public static final String QUERY_SAVE_USER = "INSERT INTO users (id, email, first_nm, last_nm, password) VALUES (?, ?, ? ,?, ?)";
  public static final String QUERY_GET_USER_AND_PASS_BY_EMAIL = "SELECT a.email, a.password FROM USERS a WHERE a.email = ?";;
  public static final String QUERY_UPDATE_USER_PROFILE = "UPDATE users SET first_nm = ?, last_nm = ? WHERE email = ?";
  public static final String QUERY_GET_USER_BALANCE = "SELECT a.id, a.balance FROM users a WHERE email = ?";
  public static final String QUERY_UPDATE_USER_BALANCE = "UPDATE users SET balance = ? WHERE email = ?";
  public static final String QUERY_GET_USER_PROFILE = "SELECT * FROM users a WHERE a.email = ?";
  public static final String QUERY_GET_USER_PROFILE_AND_IMAGE = "SELECT a.id, a.first_nm, a.last_nm, b.image_dir, a.password FROM users as a " +
      "JOIN user_pic as b on a.email = b.email " +
      "WHERE a.email = ?";
  public static final String QUERY_UPDATE_USER_PIC = "UPDATE users SET user_pic = ? WHERE email = ?";
  
// TRANSACTION TABLE
  public static final String QUERY_SAVE_TRANSACTION = "INSERT INTO transaction (id, email, invoice_no, service_cd, " +
    "service_nm, trx_type, total_amt, created_dt, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
  public static final String  QUERY_GET_TRANSACTION_HISTORY = "select a.invoice_no, a.trx_type, a.description, a.total_amt, a.created_dt from transaction a " +
      "where a.email = ? " +
      "order by a.created_dt desc ";
  
  
//  TABLE BANNER
  public static final String QUERY_GET_ALL_BANNER = "SELECT * FROM banner ORDER BY id ASC";
  
  
//  TABLE SERVICES
  public static final String QUERY_GET_ALL_SERVICES = "SELECT * FROM services ORDER BY indx ASC";
  public static final String QUERY_GET_SERVICE_BY_SERVICE_CODE = "SELECT a.service_price FROM services a WHERE a.service_cd = ?";
  public static final String QUERY_GET_SERVICE_NAME_BY_SERVICE_CODE = "SELECT a.service_cd, a.nm FROM services a WHERE a.service_cd = ?";
  
}
