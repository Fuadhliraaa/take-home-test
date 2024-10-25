package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.dto.informationdto.InfoRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class InformationServiceImpl implements InformationService{
  
  private final Logger log = LoggerFactory.getLogger(InformationServiceImpl.class);
  
  @Autowired
  private RestTemplate restTemplate;
  
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
  
}
