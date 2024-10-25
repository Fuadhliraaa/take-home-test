package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.constant.QueryConstant;
import com.assignment.test.constant.TrxConstant;
import com.assignment.test.dto.trxdto.DataDto;
import com.assignment.test.dto.trxdto.TransactionHistoryDto;
import com.assignment.test.dto.trxdto.TransactionReq;
import com.assignment.test.dto.trxdto.TransactionRes;
import com.assignment.test.utils.CommonUtils;
import com.assignment.test.utils.JWTUtils;
import com.assignment.test.utils.PreparedStatementHelper;
import com.assignment.test.utils.UserHelper;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
      
      Map<Object, Object> mapVal = new HashMap<>();
      mapVal.put("trxId", UserHelper.generateUUID());
      mapVal.put("email", email);
      mapVal.put("invoiceNo", CommonUtils.generateInvoceNo());
      mapVal.put("serviceCode", serviceCode);
      mapVal.put("serviceName", serviceName);
      mapVal.put("trxType", TrxConstant.TRX_TYPE_TOPUP);
      mapVal.put("amount", req.getTop_up_amount());
      mapVal.put("timestamp", CommonUtils.getCurrentTimestamp());
      mapVal.put("desc", TrxConstant.TRX_TOPUP_DESC);
      PreparedStatementHelper.saveTransaction(QueryConstant.QUERY_SAVE_TRANSACTION, mapVal);
      
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
  
  @Transactional
  public TransactionRes doTransaction(TransactionReq req, String token) throws JsonProcessingException {
    log.info("START - TRX SERVICE - TRANSACTION");
    TransactionRes res = new TransactionRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String BASE_URL_TRANSACTION = BaseURLConstant.SWAGGER_BASE_URL.concat("/transaction");
      
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", token);
      headers.set("Accept", "application/json");
      
      HttpEntity<TransactionReq> entity = new HttpEntity<>(req, headers);
      ResponseEntity<TransactionRes> responseEntity = restTemplate.exchange(
          BASE_URL_TRANSACTION,
          HttpMethod.POST,
          entity,
          TransactionRes.class
      );
      
      res = responseEntity.getBody();
      
      BigDecimal amount = new BigDecimal(0);
      String serviceCode = null;
      String serviceName = null;
      String trxType = null;
      
      if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null) {
        amount = responseEntity.getBody().getData().getTotal_amount();
        serviceCode = responseEntity.getBody().getData().getService_code();
        serviceName = responseEntity.getBody().getData().getService_name();
        trxType = responseEntity.getBody().getData().getTransaction_type();
      }
      
      String newToken = JWTUtils.getTokenFromAuthorizationHeader(token);
      String email = JWTUtils.getEmailFromPayload(newToken);
      
      Map<Object, Object> mapVal = new HashMap<>();
      mapVal.put("trxId", UserHelper.generateUUID());
      mapVal.put("email", email);
      mapVal.put("invoiceNo", CommonUtils.generateInvoceNo());
      mapVal.put("serviceCode", serviceCode);
      mapVal.put("serviceName", serviceName);
      mapVal.put("trxType", trxType);
      mapVal.put("amount", amount);
      mapVal.put("timestamp", CommonUtils.getCurrentTimestamp());
      mapVal.put("desc", TrxConstant.TRX_TOPUP_DESC);
      PreparedStatementHelper.saveTransaction(QueryConstant.QUERY_SAVE_TRANSACTION, mapVal);
      
      con = DriverManager.getConnection(QueryConstant.JDBC_URL, QueryConstant.USERNAME, QueryConstant.PASSWORD);
      ps = con.prepareCall(QueryConstant.QUERY_GET_USER_BALANCE);
      ps.setString(1, email);
      
      rs = ps.executeQuery();
      
      BigDecimal userBalance = new BigDecimal(0);
      while (rs.next()) {
        userBalance = rs.getBigDecimal("balance");
      }
      
      BigDecimal newUserBal = userBalance.subtract(amount);
      Map<Object, Object> updateMap = new HashMap<>();
      updateMap.put("balance", newUserBal);
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
    
    
    log.info("END - TRX SERVICE - TRANSACTION");
    return res;
  }
  
  @Override
  public TransactionRes transactionHistory(String token, String offset, String limit) throws JsonProcessingException {
    log.info("START - TRX SERVICE - TRANSACTION HISTORY");
    TransactionRes res = new TransactionRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
    
      String BASE_URL_TRX_HIST = BaseURLConstant.SWAGGER_BASE_URL.concat("/transaction/history");
      
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", token);
      headers.set("Accept", "application/json");
      
      HttpEntity<String> entity = new HttpEntity<>(headers);
      
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL_TRX_HIST)
          .queryParam("offset", offset)
          .queryParam("limit", limit);
      
      String urlWithParams = builder.toUriString();
      
      ResponseEntity<TransactionRes> responseEntity = restTemplate.exchange(
          urlWithParams,
          HttpMethod.GET,
          entity,
          TransactionRes.class
      );
      
      String newToken = JWTUtils.getTokenFromAuthorizationHeader(token);
      String email = JWTUtils.getEmailFromPayload(newToken);
      
      con = DriverManager.getConnection(QueryConstant.JDBC_URL, QueryConstant.USERNAME, QueryConstant.PASSWORD);
      ps = con.prepareCall(QueryConstant.QUERY_GET_TRANSACTION_HISTORY);
      ps.setString(1, email);
      
      rs = ps.executeQuery();
      
      List<TransactionHistoryDto> trxHistList = new ArrayList<>();
      while (rs.next()) {
        
        TransactionHistoryDto dto = new TransactionHistoryDto();
        dto.setInvoice_number(rs.getString("invoice_no"));
        dto.setTransaction_type(rs.getString("trx_type"));
        dto.setDescription(rs.getString("description"));
        dto.setTotal_amount(rs.getBigDecimal("total_amt"));
        dto.setCreated_on(rs.getTimestamp("created_dt"));
        
        trxHistList.add(dto);
        
      }
      
      DataDto dto = new DataDto();
      dto.setOffset(offset);
      dto.setLimit(limit);
      dto.setRecords(trxHistList);
      
      res.setData(dto);
      res = responseEntity.getBody();
      
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
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    
    log.info("END - TRX SERVICE - TRANSACTION HISTORY");
    return res;
  }
}
