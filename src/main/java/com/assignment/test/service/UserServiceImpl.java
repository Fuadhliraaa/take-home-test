package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.constant.QueryConstant;
import com.assignment.test.dto.userdto.LoginReq;
import com.assignment.test.dto.userdto.LoginRes;
import com.assignment.test.dto.userdto.UserReq;
import com.assignment.test.dto.userdto.UserRes;
import com.assignment.test.utils.CommonUtils;
import com.assignment.test.utils.JWTUtils;
import com.assignment.test.utils.UserHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.*;

@Service
public class UserServiceImpl implements UserService {
  
  private final Logger log = LoggerFactory.getLogger(UserService.class);
  
  @Autowired
  private RestTemplate restTemplate;
  
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
      
      ps.executeUpdate();
      
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, UserRes.class);
        
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
      while (rs.next()) {
        
        email = rs.getString("email");
        
      }
      
      res = responseEntity.getBody();
      
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
    
    log.info("END - USER SERVICE - USER LOGIN");
    return res;
  }
  
  @Override
  public UserRes updloadImage(MultipartFile file, String token) throws JsonProcessingException {
    log.info("START - USER SERVICE - UPLOAD IMAGE");
    UserRes res = new UserRes();
    String imageDir = "/src/main/resources";
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String BASE_URL_UPLOAD_IMAGE = BaseURLConstant.SWAGGER_BASE_URL.concat("/profile/image");
      
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", token);
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      
      HttpEntity<MultiValueMap<String, Object>> requestEntity = getMultiValueMapHttpEntity(file, headers);
      ResponseEntity<UserRes> responseEntity = restTemplate.exchange(
          BASE_URL_UPLOAD_IMAGE,
          HttpMethod.PUT,
          requestEntity,
          UserRes.class
      );
      
      
      String newToken = JWTUtils.getTokenFromAuthorizationHeader(token);
      String email = JWTUtils.getEmailFromPayload(newToken);
      
      String ogName = file.getOriginalFilename();
      String generateName = null;
      if (ogName != null) {
        generateName = CommonUtils.generateDynamicFileName(ogName);
      }
      
      File dir = new File(imageDir.concat("/imageprofile"));
      if (!dir.exists()) {
        dir.mkdir();
      }
      
      con = DriverManager.getConnection(QueryConstant.JDBC_URL, QueryConstant.USERNAME, QueryConstant.PASSWORD);
      ps = con.prepareCall(QueryConstant.QUERY_GET_USER_ID);
      ps.setString(1, email);
      
      rs = ps.executeQuery();
      
      String userId = null;
      while (rs.next()) {
        userId = rs.getString("id");
      }
      
      ps = con.prepareCall(QueryConstant.QUERY_SAVE_IMAGE);
      ps.setString(1, UserHelper.generateUUID());
      ps.setString(2, generateName);
      ps.setString(3, imageDir.concat("/").concat(generateName));
      ps.setInt(4, (int) file.getSize());
      ps.setString(5, userId);
      
      ps.executeUpdate();
      
      res = responseEntity.getBody();
      
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, UserRes.class);
        
      } else if (e.getStatusCode().value() == 401) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, UserRes.class);
        
      }
    } catch (IOException | SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        throw new RuntimeException();
      }
    }
    
    log.info("END - USER SERVICE - UPLOAD IMAGE");
    return res;
  }
  
  @Override
  public UserRes getUserProfile(String token) throws JsonProcessingException {
    log.info("START - USER SERVICE - GET USER PROFILE");
    UserRes res = new UserRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String BASE_URL_PROFILE = BaseURLConstant.SWAGGER_BASE_URL.concat("/profile");
      
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", token);
      headers.set("Accept", "application/json");
      
      HttpEntity<String> entity = new HttpEntity<>(headers);
      
      ResponseEntity<UserRes> response = restTemplate.exchange(BASE_URL_PROFILE, HttpMethod.GET, entity, UserRes.class);
      
      res = response.getBody();
      
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, UserRes.class);
        
      } else if (e.getStatusCode().value() == 401) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, UserRes.class);
        
      }
    }
    
    log.info("START - USER SERVICE - GET USER PROFILE");
    return res;
  }
  
  @Override
  public UserRes updateUserProfile(UserReq req, String token) throws JsonProcessingException {
    log.info("START - USER SERVICE - UPDATE USER");
    UserRes res = new UserRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    
    try {
      
      String BASE_URL_PROFILE_UPDATE = BaseURLConstant.SWAGGER_BASE_URL.concat("/profile/update");
      
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", token);
      headers.set("Accept", "application/json");
      
      HttpEntity<UserReq> entity = new HttpEntity<>(req, headers);
      ResponseEntity<UserRes> responseEntity = restTemplate.exchange(
          BASE_URL_PROFILE_UPDATE,
          HttpMethod.PUT,
          entity,
          UserRes.class
      );
      
      String newToken = JWTUtils.getTokenFromAuthorizationHeader(token);
      String email = JWTUtils.getEmailFromPayload(newToken);
      
      con = DriverManager.getConnection(QueryConstant.JDBC_URL, QueryConstant.USERNAME, QueryConstant.PASSWORD);
      ps = con.prepareCall(QueryConstant.QUERY_UPDATE_USER_PROFILE);
      ps.setString(1, req.getFirst_name());
      ps.setString(2, req.getLast_name());
      ps.setString(3, email);
      
      ps.executeUpdate();
      
      res = responseEntity.getBody();
      
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, UserRes.class);
        
        
      } else if (e.getStatusCode().value() == 401) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, UserRes.class);
        
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
    
    log.info("START - USER SERVICE - UPDATE USER");
    return res;
  }
  
  
//  STATIC METHOD
  
  private static HttpEntity<MultiValueMap<String, Object>> getMultiValueMapHttpEntity(MultipartFile file, HttpHeaders headers) throws IOException {
    HttpEntity<String> entity = new HttpEntity<>(headers);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", file.getResource());
    
    return new HttpEntity<>(body, headers);
  }
  
}
