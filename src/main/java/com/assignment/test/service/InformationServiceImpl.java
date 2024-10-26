package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.constant.QueryConstant;
import com.assignment.test.constant.ResponseConstant;
import com.assignment.test.dto.informationdto.BannerDto;
import com.assignment.test.dto.informationdto.InfoRes;
import com.assignment.test.dto.informationdto.ServiceDto;
import com.assignment.test.utils.JWTUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InformationServiceImpl implements InformationService{
  
  private final Logger log = LoggerFactory.getLogger(InformationServiceImpl.class);
  
  @Autowired
  private RestTemplate restTemplate;
  
  @Value("${spring.datasource.url}")
  public String JDBC_URL;
  
  @Value("${spring.datasource.username}")
  public String USERNAME;
  
  @Value("${spring.datasource.password}")
  public String PASSWORD;
  
  @Autowired
  private JWTUtils jwtUtils;
  
  @Override
  public InfoRes getBannerInfo(String token) throws JsonProcessingException {
    log.info("START - INFORMATION SERVICE - BANNER");
    InfoRes res = new InfoRes();
    
    try {
      
      String BASE_URL_BANNER = BaseURLConstant.SWAGGER_BASE_URL.concat("/banner");
      
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", token);
      headers.set("Accept", "application/json");
      
      HttpEntity<String> entity = new HttpEntity<>(headers);
      
      ResponseEntity<InfoRes> responseEntity = restTemplate.exchange(BASE_URL_BANNER, HttpMethod.GET, entity, InfoRes.class);
      
      res = responseEntity.getBody();
      
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, InfoRes.class);
      }
    }
    
    log.info("END - INFORMATION SERVICE - BANNER");
    return res;
  }
  
  @Override
  public InfoRes getAllServices(String token) throws JsonProcessingException {
    log.info("START - INFORMATION SERVICE - GET SERVICE");
    InfoRes res = new InfoRes();
    
    try {
      
      String BASE_URL_SERVICE = BaseURLConstant.SWAGGER_BASE_URL.concat("/services");
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", token);
      headers.set("Accept", "application/json");
      
      HttpEntity<String> entity = new HttpEntity<>(headers);
      
      ResponseEntity<InfoRes> responseEntity = restTemplate.exchange(BASE_URL_SERVICE, HttpMethod.GET, entity, InfoRes.class);
      
      res = responseEntity.getBody();
      
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, InfoRes.class);
      }
    }
    
    log.info("END - INFORMATION SERVICE - GET SERVICE");
    return res;
  }
  
  @Override
  public InfoRes newGetBannerInfo(String token) throws RuntimeException {
    log.info("START - INFORMATION SERVICE - GET BANNER");
    InfoRes res = new InfoRes();
    
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
        ps = con.prepareCall(QueryConstant.QUERY_GET_ALL_BANNER);
        
        rs = ps.executeQuery();
        List<BannerDto> banList = new ArrayList<>();
        BannerDto dto = new BannerDto();
        while (rs.next()) {
          dto = new BannerDto();
          dto.setBanner_name(rs.getString("banner_nm"));
          dto.setBanner_image(rs.getString("banner_img"));
          dto.setDescription(rs.getString("description"));
          
          banList.add(dto);
        }
        
        res.setStatus(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_SUCCESS);
        res.setData(banList);
      
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
    
    log.info("END - INFORMATION SERVICE - GET BANNER");
    return res;
  }
  
  @Override
  public InfoRes newGetAllServices(String token) throws RuntimeException {
    log.info("START - INFORMATION SERVICE - GET SERVICE");
    InfoRes res = new InfoRes();
    
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
        ps = con.prepareCall(QueryConstant.QUERY_GET_ALL_SERVICES);
        
        rs = ps.executeQuery();
        List<ServiceDto> servList = new ArrayList<>();
        ServiceDto dto = new ServiceDto();
        while (rs.next()) {
          
          dto = new ServiceDto();
          dto.setService_code(rs.getString("service_cd"));
          dto.setService_name(rs.getString("nm"));
          dto.setService_icon(rs.getString("service_icon"));
          dto.setService_tarif(rs.getBigDecimal("service_price"));
          
          servList.add(dto);
          
        }
        
        res.setStatus(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_SUCCESS);
        res.setData(servList);
        
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
    
    log.info("END - INFORMATION SERVICE - GET SERVICE");
    return res;
  }
  
}
