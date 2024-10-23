package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.constant.CommonConstant;
import com.assignment.test.constant.QueryConstant;
import com.assignment.test.dto.Data;
import com.assignment.test.dto.LoginReq;
import com.assignment.test.dto.LoginRes;
import com.assignment.test.utils.JWTUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.regex.Pattern;

@Service
public class LoginServiceImpl implements LoginService{
  
  private final Logger log = LoggerFactory.getLogger(UserService.class);
  private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
  
  @Autowired
  private RestTemplate restTemplate;
  
  @Autowired
  private EntityManager entityManager;
  
  @Override
  public LoginRes userLogin(LoginReq req) throws JsonProcessingException {
    log.info("START - LOGIN SERVICE - USER LOGIN");
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
      while (rs.next()) {
        
        email = rs.getString("email");
        
      }
      
      if (!StringUtils.isEmpty(email)) {
        
        String token = JWTUtils.generateToken(email);
        Data data = new Data();
        data.setData(token);
        
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
    
    log.info("END - LOGIN SERVICE - USER LOGIN");
    return res;
  }
}
