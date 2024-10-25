package com.assignment.test.constant;

public class QueryConstant {

//  FOR PREPARED STATEMENT
  public static final String JDBC_URL = "jdbc:postgresql://localhost:5432/NUTECH_TRX";
  public static final String USERNAME = "postgres";
  public static final String PASSWORD = "root";

//  TABLE USER
  public static final String QUERY_SAVE_USER = "INSERT INTO users (id, email, first_nm, last_nm, password) VALUES (?, ?, ? ,?, ?)";
  public static final String QUERY_GET_USER_BY_EMAIL = "SELECT a.email, a.password FROM USERS a WHERE a.email = ?";;
  public static final String QUERY_GET_USER_ID = "SELECT a.id FROM USERS a WHERE a.EMAIL = ?";
  public static final String QUERY_UPDATE_USER_PROFILE = "UPDATE users SET first_nm = ?, last_nm = ? WHERE email = ?";
  public static final String QUERY_GET_USER_BALANCE = "SELECT a.id, a.balance FROM users a WHERE a.email = ?";
  public static final String QUERY_UPDATE_USER_BALANCE = "UPDATE users SET balance = ? WHERE email = ?";
  
//  IMAGE TABLE
  public static final String QUERY_SAVE_IMAGE = "INSERT INTO user_pic (id, image_nm, image_dir, image_size, user_id) VALUES " +
    "(?, ?, ?, ?, ?)";

  
// TRANSACTION TABLE
  public static final String QUERY_SAVE_TRANSACTION = "INSERT INTO transaction (id, email, invoice_no, service_cd, " +
    "service_nm, trx_type, total_amt, created_dt, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
  public static final String  QUERY_GET_TRANSACTION_HISTORY = "select a.invoice_no, a.trx_type, a.description, a.total_amt, a.created_dt from transaction a " +
      "where a.email = ? " +
      "order by a.created_dt desc";
}
