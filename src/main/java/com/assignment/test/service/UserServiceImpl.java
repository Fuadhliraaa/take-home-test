package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.constant.CommonConstant;
import com.assignment.test.constant.QueryConstant;
import com.assignment.test.dto.*;
import com.assignment.test.utils.JWTUtils;
import com.assignment.test.utils.UserHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.*;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService{

  private final Logger log = LoggerFactory.getLogger(UserService.class);
  private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private EntityManager entityManager;

  @Transactional
  public UserRes userRegistration(UserReq req) throws RuntimeException, JsonProcessingException {
    log.info("START - USER SERVICE - USER REGISTRATION");
    UserRes res = new UserRes();
    
    String jdbcUrl = QueryConstant.JDBC_URL;
    String username = QueryConstant.USERNAME;
    String password = QueryConstant.PASSWORD;
    
    Connection connection = null;
    PreparedStatement ps = null;

    try {
      String BASE_URL_REGIS = BaseURLConstant.SWAGGER_BASE_URL.concat("/registration");
      ResponseEntity<UserRes> responseEntity = restTemplate.postForEntity(BASE_URL_REGIS, req, UserRes.class);

      res = responseEntity.getBody();
      
      connection = DriverManager.getConnection(jdbcUrl, username, password);
      
      ps = connection.prepareCall(QueryConstant.QUERY_SAVE_USER);
      
      ps.setString(1, UserHelper.generateUUID());
      ps.setString(2, req.getEmail());
      ps.setString(3, req.getFirst_name());
      ps.setString(4, req.getLast_name());
      ps.setString(5, req.getPassword());
      
      int rowInserted = ps.executeUpdate();
      
      if (rowInserted > 0) {
        log.info("Successfully save to Database");
      }

    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, UserRes.class);
        
        log.info("Error, " + e);

      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (ps != null) ps.close();
        if (connection != null) connection.close();
      } catch (SQLException e) {
        throw new RuntimeException();
      }
    }
    
    log.info("END - USER SERVICE - USER REGISTRATION");
    return res;
  }
  
  @Override
  public LoginRes userLogin(LoginReq req) throws JsonProcessingException {
    log.info("START - USER SERVICE - USER LOGIN");
    LoginRes res = new LoginRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String BASE_URL_LOGIN = BaseURLConstant.SWAGGER_BASE_URL.concat("/login");
      ResponseEntity<LoginRes> responseEntity = restTemplate.postForEntity(BASE_URL_LOGIN, req, LoginRes.class);
      
      
      con = DriverManager.getConnection(QueryConstant.JDBC_URL, QueryConstant.USERNAME, QueryConstant.PASSWORD);
      ps = con.prepareCall(QueryConstant.QUERY_GET_USER_BY_EMAIL);
      ps.setString(1, req.getEmail());
      
      rs = ps.executeQuery();
      
      String email = null;
      String password = null;
      while (rs.next()) {
        
        email = rs.getString("email");
        password = rs.getString("password");
        
      }
      
      if (!StringUtils.isEmpty(email)) {
        
        Data data = new Data();
        if (req.getPassword().equals(password)) {
          String token = JWTUtils.generateToken(email);
          data.setData(token);
        }
        
        res.setData(data);
        res = responseEntity.getBody();
        
      } else {
        res.setMessage(CommonConstant.STATUS_CODE_NOTREGISTED);
        res.setMessage(CommonConstant.STATUS_MESSAGE_USER_NOT_REGISTRED);
        res.setData(null);
      }
      
    } catch (HttpClientErrorException e) {
      
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, LoginRes.class);
        
        log.info("Error, " + e);
      }
      
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
    
    log.info("END - USER SERVICE - USER LOGIN");
    return res;
  }

}
