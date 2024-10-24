package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.constant.QueryConstant;
import com.assignment.test.constant.TrxConstant;
import com.assignment.test.dto.trxdto.TransactionReq;
import com.assignment.test.dto.trxdto.TransactionRes;
import com.assignment.test.utils.JWTUtils;
import com.assignment.test.utils.PreparedStatementHelper;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionServiceImpl implements TransactionService {
  
  private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
  
  @Autowired
  private RestTemplate restTemplate;
  
  @Override
  public TransactionRes getBalance(String token) throws JsonProcessingException {
    log.info("START - TRX SERVICE - GET BALANCE");
    TransactionRes res = new TransactionRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String BASE_URL_GET_BALANCE = BaseURLConstant.SWAGGER_BASE_URL.concat("/balance");
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", token);
      headers.set("Accept", "application/json");
      
      HttpEntity<String> entity = new HttpEntity<>(headers);
      
      ResponseEntity<TransactionRes> responseEntity = restTemplate.exchange(
          BASE_URL_GET_BALANCE,
          HttpMethod.GET,
          entity,
          TransactionRes.class
      );
      
      res = responseEntity.getBody();
      
      BigDecimal balance = new BigDecimal(0);
      if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null) {
        balance = responseEntity.getBody().getData().getBalance();
      }
      
      String newToken = JWTUtils.getTokenFromAuthorizationHeader(token);
      String email = JWTUtils.getEmailFromPayload(newToken);
      
      Map<Object, Object> updateMap = new HashMap<>();
      updateMap.put("balance", balance);
      updateMap.put("email", email);
      PreparedStatementHelper.updateUserBalance(QueryConstant.QUERY_UPDATE_USER_BALANCE, updateMap);
      
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, TransactionRes.class);
        
        
      } else if (e.getStatusCode().value() == 401) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, TransactionRes.class);
        
      }
    }
    
    log.info("END - TRX SERVICE - GET BALANCE");
    return res;
  }
  
  @Transactional
  public TransactionRes topUpBalance(TransactionReq req, String token) throws JsonProcessingException {
    log.info("START - TRX SERVICE - TOP UP BALANCE");
    TransactionRes res = new TransactionRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String BASE_URL_TOP_UP = BaseURLConstant.SWAGGER_BASE_URL.concat("/topup");
      
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", token);
      headers.set("Accept", "application/json");
      
      HttpEntity<TransactionReq> entity = new HttpEntity<>(req, headers);
      ResponseEntity<TransactionRes> responseEntity = restTemplate.exchange(
          BASE_URL_TOP_UP,
          HttpMethod.POST,
          entity,
          TransactionRes.class
      );
      
      res = responseEntity.getBody();
      
      BigDecimal balance = new BigDecimal(0);
      String serviceCode = TrxConstant.TRX_TYPE_TOPUP;
      String serviceName = TrxConstant.TRX_TYPE_TOPUP;
      if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null) {
        balance = responseEntity.getBody().getData().getBalance();
      }
      
      String newToken = JWTUtils.getTokenFromAuthorizationHeader(token);
      String email = JWTUtils.getEmailFromPayload(newToken);
      
      Map<Object, Object> updateMap = new HashMap<>();
      updateMap.put("balance", balance);
      updateMap.put("email", email);
      PreparedStatementHelper.updateUserBalance(QueryConstant.QUERY_UPDATE_USER_BALANCE, updateMap);
      
      if (balance.compareTo(BigDecimal.ZERO) == 0) {
        Map<Object, Object> mapVal = new HashMap<>();
        mapVal.put("email", email);
        mapVal.put("serviceCode", serviceCode);
        mapVal.put("serviceName", serviceName);
        mapVal.put("amount", req.getTop_up_amount());
        mapVal.put("desc", TrxConstant.TRX_TOPUP_DESC);
        PreparedStatementHelper.saveTransaction(QueryConstant.QUERY_SAVE_TRANSACTION, mapVal);
      }
      
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, TransactionRes.class);
        
        
      } else if (e.getStatusCode().value() == 401) {
        String errorResponse = e.getResponseBodyAsString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, TransactionRes.class);
        
      }
    }
    
    log.info("START - TRX SERVICE - TOP UP BALANCE");
    return res;
  }
}
