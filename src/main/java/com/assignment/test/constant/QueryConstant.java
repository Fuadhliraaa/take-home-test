package com.assignment.test.constant;

public class QueryConstant {

//  FOR PREPARED STATEMENT
  public static final String JDBC_URL = "jdbc:postgresql://localhost:5432/NUTECH_TRX";
  public static final String USERNAME = "postgres";
  public static final String PASSWORD = "root";

//  TABLE USER
  public static final String QUERY_SAVE_USER = "INSERT INTO users (id, email, first_nm, last_nm, password) VALUES (?, ?, ? ,?, ?)";
  public static final String QUERY_GET_USER_BY_EMAIL = "SELECT a.email, a.password FROM USERS a WHERE a.email = ?";
  public static final String QUERY_GET_USER_ID = "SELECT a.id FROM USERS a WHERE a.EMAIL = ?";
  
//  IMAGE TABLE
  public static final String QUERY_SAVE_IMAGE = "INSERT INTO user_pic (id, image_nm, image_dir, image_size, user_id) VALUES " +
    "(?, ?, ?, ?, ?)";

}
