package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.constant.QueryConstant;
import com.assignment.test.constant.ResponseConstant;
import com.assignment.test.constant.TrxConstant;
import com.assignment.test.dto.informationdto.ServiceDto;
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
import org.springframework.beans.factory.annotation.Value;
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
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionServiceImpl implements TransactionService {
  
  private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
  
  @Value("${spring.datasource.url}")
  public String JDBC_URL;
  
  @Value("${spring.datasource.username}")
  public String USERNAME;
  
  @Value("${spring.datasource.password}")
  public String PASSWORD;
  
  @Autowired
  private JWTUtils jwtUtils;
  
  @Override
  public TransactionRes newGetBalance(String token) throws RuntimeException {
    log.info("START - TRX SERVICE - GET BALANCE");
    TransactionRes res = new TransactionRes();
    
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
        ps = con.prepareCall(QueryConstant.QUERY_GET_USER_BALANCE);
        ps.setString(1, email);
        
        rs = ps.executeQuery();
        
        BigDecimal balance = new BigDecimal(0);
        DataDto dto = new DataDto();
        while (rs.next()) {
          
          if (rs.getBigDecimal("balance") == null) {
            Map<String, Object> balMap = new HashMap<>();
            balMap.put("balance", balance);
            balMap.put("email", email);
            PreparedStatementHelper.updateUserBalance(JDBC_URL, USERNAME, PASSWORD, QueryConstant.QUERY_UPDATE_USER_BALANCE, balMap);
            
            dto.setBalance(BigDecimal.valueOf(0));
          } else {
            dto.setBalance(rs.getBigDecimal("balance"));
          }
          
        }
        
        res.setData(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_SUCCESS);
        res.setData(dto);
        
      }
      
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
    
    log.info("END - TRX SERVICE - GET BALANCE");
    return res;
  }
  
  @Override
  public TransactionRes newTopUpBalance(TransactionReq req, String token) throws RuntimeException {
    log.info("START - TRX SERVICE - TOP UP BALANCE");
    TransactionRes res = new TransactionRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String newToken = jwtUtils.getTokenFromAuthorizationHeader(token);
      String email = jwtUtils.extractEmail(newToken);
      
      if (!jwtUtils.validateToken(token, email)) {
        res.setStatus(ResponseConstant.STATUS_CODE_108);
        res.setMessage(ResponseConstant.STATUS_DESC_UNAUTHORIZED);
      } else if (req.getTop_up_amount() <= 0) {
        res.setStatus(ResponseConstant.STATUS_CODE_102);
        res.setMessage(ResponseConstant.STATUS_DESC_WRONG_BALANCE_NOMINAL);
      } else {
        
        con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        ps = con.prepareCall(QueryConstant.QUERY_GET_USER_BALANCE);
        ps.setString(1, email);
        
        rs = ps.executeQuery();
        BigDecimal balDb = new BigDecimal(0);
        while (rs.next()) {
          balDb = rs.getBigDecimal("balance");
        }
        
        BigDecimal newBalance = new BigDecimal(0);
        if (balDb == null) {
          newBalance = new BigDecimal(req.getTop_up_amount());
        } else {
          newBalance = balDb.add(new BigDecimal(req.getTop_up_amount()));
        }
        
        Map<String, Object> balMap = new HashMap<>();
        balMap.put("balance", newBalance);
        balMap.put("email", email);
        PreparedStatementHelper.updateUserBalance(JDBC_URL, USERNAME, PASSWORD, QueryConstant.QUERY_UPDATE_USER_BALANCE, balMap);
        
        Map<Object, Object> mapVal = new HashMap<>();
        mapVal.put("trxId", UserHelper.generateUUID());
        mapVal.put("email", email);
        mapVal.put("invoiceNo", CommonUtils.generateInvoceNo());
        mapVal.put("serviceCode", TrxConstant.TRX_TYPE_TOPUP);
        mapVal.put("serviceName", TrxConstant.TRX_TOPUP_SERVICE);
        mapVal.put("trxType", TrxConstant.TRX_TYPE_TOPUP);
        mapVal.put("amount", newBalance);
        mapVal.put("timestamp", CommonUtils.getCurrentTimestamp());
        mapVal.put("desc", TrxConstant.TRX_TOPUP_DESC);
        PreparedStatementHelper.saveTransaction(JDBC_URL, USERNAME, PASSWORD, QueryConstant.QUERY_SAVE_TRANSACTION, mapVal);
        
        DataDto dto = new DataDto();
        dto.setBalance(newBalance);
        
        res.setStatus(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_SUCCESS_TOPUP);
        res.setData(dto);
        
      }
      
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
    
    log.info("END - TRX SERVICE - TOP UP BALANCE");
    return res;
  }
  
  @Transactional
  public TransactionRes newDoTransaction(TransactionReq req, String token) throws RuntimeException {
    log.info("START - TRX SERVICE - TRX");
    TransactionRes res = new TransactionRes();
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
      
      String newToken = jwtUtils.getTokenFromAuthorizationHeader(token);
      String email = jwtUtils.extractEmail(newToken);
      
      // BALANCE VALIDATION
      con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
      ps = con.prepareCall(QueryConstant.QUERY_GET_USER_BALANCE);
      ps.setString(1, email);
      
      rs = ps.executeQuery();
      BigDecimal balDb = new BigDecimal(0);
      while (rs.next()) {
        balDb = rs.getBigDecimal("balance");
      }
      
      // AVAILABLE SERVICE VALIDATION
      ps = con.prepareCall(QueryConstant.QUERY_GET_ALL_SERVICES);
      rs = ps.executeQuery();
      List<String> serviceList = new ArrayList<>();
      while (rs.next()) {
        
        String serviceCd = rs.getString("service_cd");
        serviceList.add(serviceCd);
        
      }
      
      ps = con.prepareCall(QueryConstant.QUERY_GET_SERVICE_BY_SERVICE_CODE);
      ps.setString(1, req.getService_code().toUpperCase());
      rs = ps.executeQuery();
      BigDecimal servicePrice = new BigDecimal(0);
      while (rs.next()) {
        servicePrice = rs.getBigDecimal("service_price");
      }
      
      ps = con.prepareCall(QueryConstant.QUERY_GET_SERVICE_NAME_BY_SERVICE_CODE);
      ps.setString(1, req.getService_code().toUpperCase());
      rs = ps.executeQuery();
      String serviceName = null;
      String serviceCode = null;
      while (rs.next()) {
        
        serviceName = rs.getString("nm");
        serviceCode = rs.getString("service_cd");
        
      }
      
      if (!jwtUtils.validateToken(token, email)) {
        res.setStatus(ResponseConstant.STATUS_CODE_108);
        res.setMessage(ResponseConstant.STATUS_DESC_UNAUTHORIZED);
      } else if (balDb.compareTo(servicePrice) < 0) {
        res.setStatus(ResponseConstant.STATUS_CODE_102);
        res.setMessage(ResponseConstant.STATUS_DESC_INSUFICIENCE_BALANCE);
      } else if (!serviceList.contains(req.getService_code().toUpperCase())) {
        res.setStatus(ResponseConstant.STATUS_CODE_102);
        res.setMessage(ResponseConstant.STATUS_DESC_SERVICE_UNDIFINED);
      } else {
        
        Map<Object, Object> mapVal = new HashMap<>();
        mapVal.put("trxId", UserHelper.generateUUID());
        mapVal.put("email", email);
        mapVal.put("invoiceNo", CommonUtils.generateInvoceNo());
        mapVal.put("serviceCode", serviceCode);
        mapVal.put("serviceName", serviceName);
        mapVal.put("trxType", TrxConstant.TRX_TYPE_PAYMENT);
        mapVal.put("amount", servicePrice);
        mapVal.put("timestamp", CommonUtils.getCurrentTimestamp());
        mapVal.put("desc", serviceName);
        PreparedStatementHelper.saveTransaction(JDBC_URL, USERNAME, PASSWORD, QueryConstant.QUERY_SAVE_TRANSACTION, mapVal);
        
        BigDecimal newUserBal = balDb.subtract(servicePrice);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("balance", newUserBal);
        updateMap.put("email", email);
        PreparedStatementHelper.updateUserBalance(JDBC_URL, USERNAME, PASSWORD, QueryConstant.QUERY_UPDATE_USER_BALANCE, updateMap);
        
        DataDto dto = new DataDto();
        dto.setInvoice_number(mapVal.get("invoiceNo").toString());
        dto.setService_code(serviceCode);
        dto.setService_name(serviceName);
        dto.setTransaction_type(mapVal.get("trxType").toString());
        dto.setTotal_amount(servicePrice);
        dto.setCreated_on((Timestamp) mapVal.get("timestamp"));
        
        res.setStatus(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_TRX_SUCCESS);
        res.setData(dto);
        
      }
      
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
    
    log.info("END - TRX SERVICE - TRX");
    return res;
  }
  
  @Transactional
  public TransactionRes newTransactionHistory(String token, int offset, int limit) throws RuntimeException {
    log.info("START - TRX SERVICE - TRX HISTORY");
    TransactionRes res = new TransactionRes();
    
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
        
        boolean usePagination = (limit > 0);
        String sql = QueryConstant.QUERY_GET_TRANSACTION_HISTORY;
        if (usePagination) {
          
          sql += "LIMIT ? OFFSET ?";
          
          ps = con.prepareCall(sql);
          ps.setString(1, email);
          ps.setInt(2, limit);
          ps.setInt(3, offset);
          
        } else {
          
          ps = con.prepareCall(sql);
          ps.setString(1, email);
          
        }
        log.info("SQL STATEMENT => " + sql);
        
        rs = ps.executeQuery();
        
        List<TransactionHistoryDto> trxHistDto = new ArrayList<>();
        while (rs.next()) {
          
          TransactionHistoryDto dto = new TransactionHistoryDto();
          dto.setInvoice_number(rs.getString("invoice_no"));
          dto.setTransaction_type(rs.getString("trx_type"));
          dto.setDescription(rs.getString("description"));
          dto.setTotal_amount(rs.getBigDecimal("total_amt"));
          dto.setCreated_on(rs.getTimestamp("created_dt"));
          
          trxHistDto.add(dto);
          
        }
        
        DataDto data = new DataDto();
        data.setOffset(String.valueOf(offset));
        data.setLimit(String.valueOf(limit));
        data.setRecords(trxHistDto);
        
        res.setData(data);
        res.setStatus(ResponseConstant.STATUS_CODE_0);
        res.setMessage(ResponseConstant.STATUS_DESC_SUCCESSFUL_GET_TRX_HIST);
        
      }
      
      
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
    
    log.info("END - TRX SERVICE - TRX HISTORY");
    return res;
  }
}
