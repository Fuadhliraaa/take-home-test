package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.constant.CommonConstant;
import com.assignment.test.constant.QueryConstant;
import com.assignment.test.dto.UserReq;
import com.assignment.test.dto.UserRes;
import com.assignment.test.utils.UserHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        
        log.error("Error, " + e);

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

}
