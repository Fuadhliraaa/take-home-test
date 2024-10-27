package com.assignment.test.service;

import com.assignment.test.constant.QueryConstant;
import com.assignment.test.constant.ResponseConstant;
import com.assignment.test.dto.userdto.*;
import com.assignment.test.utils.CommonUtils;
import com.assignment.test.utils.JWTUtils;
import com.assignment.test.utils.PreparedStatementHelper;
import com.assignment.test.utils.UserHelper;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
  
  private final Logger log = LoggerFactory.getLogger(UserService.class);
  
  private static final String EMAIL_REGEX = "^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,7}$";
  private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
  private final String[] ALLOWED_FORMATS = {"image/jpeg", "image/png"};
  
  @Autowired
  private JWTUtils jwtUtils;
  
  @Value("${spring.datasource.url}")
  public String JDBC_URL;
  
  @Value("${spring.datasource.username}")
  public String USERNAME;
  
  @Value("${spring.datasource.password}")
  public String PASSWORD;
  
  @Override
  public UserRes newUserRegistration(UserReq req) throws RuntimeException {
    log.info("START - USER SERVICE - USER REGISTRATION");
    UserRes res = new UserRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
      ps = con.prepareCall(QueryConstant.QUERY_GET_USER_PROFILE);
      ps.setString(1, req.getEmail());
      
      rs = ps.executeQuery();
      
      String emailDb = null;
      while (rs.next()) {
        emailDb = rs.getString("email");
      }
      
      if (!EMAIL_PATTERN.matcher(req.getEmail()).matches()) {
        res.setStatus(ResponseConstant.STATUS_CODE_102);
        res.setMessage(ResponseConstant.STATUS_DESC_EMAIL_FORMAT_ERROR);
      } else if (req.getPassword().length() - 1 < 8) {
        res.setStatus(ResponseConstant.STATUS_CODE_102);
        res.setMessage(ResponseConstant.STATUS_DESC_PASSWORD_LEGNTH);
      } else if (!StringUtils.isEmpty(emailDb)) {
        res.setStatus(ResponseConstant.STATUS_CODE_102);
        res.setMessage(ResponseConstant.STATUS_DESC_USER_ALREADY_REGISTERED);
      } else {
        
        Map<String, Object> usrMap = new HashMap<>();
        usrMap.put("uuid", UserHelper.generateUUID());
        usrMap.put("email", req.getEmail());
        usrMap.put("firstName", req.getFirst_name());
        usrMap.put("lastName", req.getLast_name());
        usrMap.put("password", req.getPassword());
        PreparedStatementHelper.saveUser(JDBC_URL, USERNAME, PASSWORD, QueryConstant.QUERY_SAVE_USER, usrMap);
        
        res.setStatus(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_REGISTED_SUCCES);
        
      }
      
    } catch (RuntimeException e) {
      log.error("Error ", e);
      throw new RuntimeException();
    } catch (SQLException e) {
      log.error("Error ", e);
      throw new RuntimeException(e);
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        log.error("Error ", e);
        throw new RuntimeException();
      }
    }
    
    log.info("END - USER SERVICE - USER REGISTRATION");
    return res;
  }
  
  @Override
  public LoginRes newUserLogin(LoginReq req) throws RuntimeException {
    log.info("END - USER SERVICE - USER LOGIN");
    LoginRes res = new LoginRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
      ps = con.prepareCall(QueryConstant.QUERY_GET_USER_AND_PASS_BY_EMAIL);
      ps.setString(1, req.getEmail());
      
      rs = ps.executeQuery();
      
      String passDb = null;
      String emailDb = null;
      while (rs.next()) {
        emailDb = rs.getString("email");
        passDb = rs.getString("password");
      }
      
      if (!EMAIL_PATTERN.matcher(req.getEmail()).matches()) {
        res.setStatus(ResponseConstant.STATUS_CODE_102);
        res.setMessage(ResponseConstant.STATUS_DESC_EMAIL_FORMAT_ERROR);
      } else if (req.getPassword().length() - 1 < 8) {
        res.setStatus(ResponseConstant.STATUS_CODE_102);
        res.setMessage(ResponseConstant.STATUS_DESC_PASSWORD_LEGNTH);
      } else if (!req.getPassword().equals(passDb) || !emailDb.equalsIgnoreCase(req.getEmail())) {
        res.setStatus(ResponseConstant.STATUS_CODE_103);
        res.setMessage(ResponseConstant.STATUS_DESC_WRONG_USERNAME_OR_PASSWORD);
      } else {
        
        String token = jwtUtils.generateToken(req.getEmail());
        
        LoginDto dto = new LoginDto();
        dto.setToken(token);
        
        res.setData(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_SUCCESSFULLY_LOGIN);
        res.setData(dto);
        
      }
      
    } catch (RuntimeException e) {
      log.error("Error ", e);
      throw new RuntimeException();
    } catch (SQLException e) {
      log.error("Error ", e);
      throw new RuntimeException(e);
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        log.error("Error ", e);
        throw new RuntimeException();
      }
    }
    
    log.info("END - USER SERVICE - USER LOGIN");
    return res;
  }
  
  @Override
  public UserRes uploadImage(MultipartFile file, String token) throws RuntimeException {
    log.info("START - USER SERVICE - UPLOAD PROFILE");
    UserRes res = new UserRes();
    
    String imageDir = "/src/main/resources";
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String newToken = jwtUtils.getTokenFromAuthorizationHeader(token);
      String email = jwtUtils.extractEmail(newToken);
      
      String fileType = file.getContentType();
      
      if (!Arrays.asList(ALLOWED_FORMATS).contains(fileType)) {
        res.setStatus(ResponseConstant.STATUS_CODE_102);
        res.setMessage(ResponseConstant.STATUS_DESC_WRONG_IMAGE_FORMAT);
      } else if (!jwtUtils.validateToken(token, email)) {
        res.setStatus(ResponseConstant.STATUS_CODE_108);
        res.setMessage(ResponseConstant.STATUS_DESC_UNAUTHORIZED);
      } else {
        
        String ogName = file.getOriginalFilename();
        String generateName = null;
        if (ogName != null) {
          generateName = CommonUtils.generateDynamicFileName(ogName);
        }
        
        con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        ps = con.prepareCall(QueryConstant.QUERY_GET_USER_PROFILE);
        ps.setString(1, email);
        
        rs = ps.executeQuery();
        
        String firstName = null;
        String lastName = null;
        while (rs.next()) {
          firstName = rs.getString("first_nm");
          lastName = rs.getString("last_nm");
        }
        
        Map<String, Object> usrPicMap = new HashMap<>();
        usrPicMap.put("userPic", imageDir.concat("/").concat(generateName));
        usrPicMap.put("email", email);
        PreparedStatementHelper.updateUserPic(JDBC_URL, USERNAME, PASSWORD, QueryConstant.QUERY_UPDATE_USER_PIC, usrPicMap);
        
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setFirst_name(firstName);
        dto.setLast_name(lastName);
        dto.setProfile_image(usrPicMap.get("userPic").toString());
        
        res.setStatus(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_SUCCESSFULLY_UPDATE_PROFILE_PIC);
        res.setData(dto);
        
      }
      
      
    } catch (RuntimeException e) {
      log.error("Error ", e);
      throw new RuntimeException();
    } catch (SQLException e) {
      log.error("Error ", e);
      throw new RuntimeException(e);
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        log.error("Error ", e);
        throw new RuntimeException();
      }
    }
    
    log.info("END - USER SERVICE - UPLOAD PROFILE");
    return res;
  }
  
  @Override
  public UserRes newGetUserProfile(String token) throws RuntimeException {
    log.info("START - USER SERVICE - GET USER PROFILE");
    UserRes res = new UserRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String newToken = jwtUtils.getTokenFromAuthorizationHeader(token);
      String email = jwtUtils.extractEmail(newToken);
      
      if (!jwtUtils.validateToken(token, email)) {
        res.setStatus(ResponseConstant.STATUS_CODE_108);
        res.setMessage(ResponseConstant.STATUS_DESC_UNAUTHORIZED);
      } else {
        
        con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        UserDto dto = new UserDto();
        ps = con.prepareCall(QueryConstant.QUERY_GET_USER_PROFILE);
        ps.setString(1, email);
        
        rs = ps.executeQuery();
        
        while (rs.next()) {
          
          dto.setEmail(email);
          dto.setFirst_name(rs.getString("first_nm"));
          dto.setLast_name(rs.getString("last_nm"));
          dto.setProfile_image(rs.getString("user_pic"));
          
        }
        
        res.setData(dto);
        res.setStatus(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_SUCCESS);
        
      }
      
      
    } catch (RuntimeException e) {
      log.error("Error ", e);
      throw new RuntimeException();
    } catch (SQLException e) {
      log.error("Error ", e);
      throw new RuntimeException(e);
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        log.error("Error ", e);
        throw new RuntimeException();
      }
    }
    
    log.info("END - USER SERVICE - GET USER PROFILE");
    return res;
  }
  
  @Override
  public UserRes newUpdateUserProfile(UserReq req, String token) throws RuntimeException {
    log.info("START - USER SERVICE - UPDATE USER PROFILE");
    UserRes res = new UserRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String newToken = jwtUtils.getTokenFromAuthorizationHeader(token);
      String email = jwtUtils.extractEmail(newToken);
      
      if (!jwtUtils.validateToken(token, email)) {
        res.setStatus(ResponseConstant.STATUS_CODE_108);
        res.setMessage(ResponseConstant.STATUS_DESC_UNAUTHORIZED);
      } else {
        
        
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", req.getFirst_name());
        userMap.put("lastName", req.getLast_name());
        userMap.put("email", email);
        PreparedStatementHelper.updateUserProfile(JDBC_URL, USERNAME, PASSWORD, QueryConstant.QUERY_UPDATE_USER_PROFILE, userMap);
        
        con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        ps = con.prepareCall(QueryConstant.QUERY_GET_USER_PROFILE_AND_IMAGE);
        ps.setString(1, email);
        
        rs = ps.executeQuery();
        String firstName = null;
        String lastName = null;
        String image = null;
        while (rs.next()) {
          firstName = rs.getString("first_nm");
          lastName = rs.getString("last_nm");
          image = rs.getString("image_dir");
        }
        
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setFirst_name(firstName);
        dto.setLast_name(lastName);
        dto.setProfile_image(image);
        
        res.setStatus(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_SUCCESS_UPDATE_PROFILE);
        res.setData(dto);
        
      }
      
    } catch (RuntimeException e) {
      log.error("Error ", e);
      throw new RuntimeException();
    } catch (SQLException e) {
      log.error("Error ", e);
      throw new RuntimeException(e);
    } finally {
      try {
        if (ps != null) ps.close();
        if (con != null) con.close();
      } catch (SQLException e) {
        log.error("Error ", e);
        throw new RuntimeException();
      }
    }
    
    log.info("END - USER SERVICE - UPDATE USER PROFILE");
    return res;
  }
  
}
