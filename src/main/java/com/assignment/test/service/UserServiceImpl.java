package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.constant.CommonConstant;
import com.assignment.test.constant.QueryConstant;
import com.assignment.test.dto.*;
import com.assignment.test.security.JwtService;
import com.assignment.test.utils.CommonUtils;
import com.assignment.test.utils.UserHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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
public class UserServiceImpl implements UserService{

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private RestTemplate restTemplate;
  
  @Autowired
  private JwtService jwtService;

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
          String token = jwtService.generateToken(email);
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
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      headers.setBearerAuth(CommonUtils.getAuthToken(token));
      
      ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
        @Override
        public String getFilename() {
          return file.getOriginalFilename();  // Return the actual file name
        }
      };
      
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("file", fileAsResource);
      
      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
      ResponseEntity<UserRes> responseEntity = restTemplate.exchange(
          BASE_URL_UPLOAD_IMAGE,
          HttpMethod.PUT,
          requestEntity,
          UserRes.class
      );
      
      res = responseEntity.getBody();
      
      String ogName = file.getOriginalFilename();
      String generateName = null;
      if (ogName != null) {
        generateName = CommonUtils.generateDynamicFileName(ogName);
      }
      
      File dir = new File(imageDir);
      if (!dir.exists()) {
        dir.mkdir();
      }
      
      con = DriverManager.getConnection(QueryConstant.JDBC_URL, QueryConstant.USERNAME, QueryConstant.PASSWORD);
      ps = con.prepareCall(QueryConstant.QUERY_GET_USER_ID);
      
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
      ResponseEntity<UserRes> responseEntity = restTemplate.getForEntity(BASE_URL_PROFILE, UserRes.class);
      
      String email = jwtService.extractUsername(token);
      
      con = DriverManager.getConnection(QueryConstant.JDBC_URL, QueryConstant.USERNAME, QueryConstant.PASSWORD);
      ps = con.prepareCall(QueryConstant.QUERY_GET_USER_BY_EMAIL);
      ps.setString(1, email);
      
      rs = ps.executeQuery();
      
      String firstName = null;
      String lastName = null;
      String profileImage = null;
      while (rs.next()) {
        
        firstName = rs.getString("first_nm");
        lastName = rs.getString("last_name");
        
      }
      
      UserDto dto = new UserDto();
      dto.setEmail(email);
      dto.setFirst_name(firstName);
      dto.setLast_name(lastName);
      dto.setImage(profileImage);
      
      res.setData(dto);
      res = responseEntity.getBody();
    
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
        if (con != null) con.close();
      } catch (SQLException e) {
        throw new RuntimeException();
      }
    }
    
    log.info("START - USER SERVICE - GET USER PROFILE");
    return res;
  }
  
}
