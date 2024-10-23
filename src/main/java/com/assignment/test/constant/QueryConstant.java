package com.assignment.test.constant;

public class QueryConstant {

//  FOR PREPARED STATEMENT
  public static final String JDBC_URL = "jdbc:postgresql://localhost:5432/NUTECH_TRX";
  public static final String USERNAME = "postgres";
  public static final String PASSWORD = "root";

  public static final String QUERY_SAVE_USER = "INSERT INTO users (id, email, first_nm, last_nm, password) VALUES (?, ?, ? ,?, ?)";
  public static final String QUERY_GET_USER_BY_EMAIL = "SELECT a.email FROM USERS a WHERE a.email = ?";

}
