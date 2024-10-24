package com.assignment.test.controller;

import com.assignment.test.dto.trxdto.TransactionReq;
import com.assignment.test.dto.trxdto.TransactionRes;
import com.assignment.test.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trx")
public class TransactionController {
  
  private final Logger log = LoggerFactory.getLogger(TransactionController.class);
  
  @Autowired
  private TransactionService transactionService;
  
  @GetMapping("/balance")
  public ResponseEntity getUserBalance(@RequestHeader("Authorization") String token) throws JsonProcessingException {
    log.info("START - TRANSACTION CONTROLLER - GET BALANCE");
    TransactionRes res = transactionService.getBalance(token);
    log.info("FINISH - TRANSACTION CONTROLLER - GET BALANCE");
    return ResponseEntity.ok(res);
  }
  
  @PostMapping("/topup")
  public ResponseEntity topUpBalance(@RequestBody TransactionReq req,
                                     @RequestHeader("Authorization") String token) throws JsonProcessingException {
    log.info("START - TRANSACTION CONTROLLER - TOP UP BALANCE");
    TransactionRes res = transactionService.topUpBalance(req, token);
    log.info("FINISH - TRANSACTION CONTROLLER - TOP UP BALANCE");
    return ResponseEntity.ok(res);
  }
  
  @PostMapping("/transaction")
  public ResponseEntity doTrancsaction(@RequestBody TransactionReq req,
                                       @RequestHeader("Authorization") String token) throws JsonProcessingException {
    log.info("START - TRANSACTION CONTROLLER - TRANSACTION");
    TransactionRes res = transactionService.doTransaction(req, token);
    log.info("FINISH - TRANSACTION CONTROLLER - TRANSACTION");
    return ResponseEntity.ok(res);
  }
  
  @GetMapping("/transaction/history")
  public ResponseEntity getTransactionHistory(@RequestHeader("Authorization") String token,
                                              @RequestParam(defaultValue = "0") String offset,
                                              @RequestParam(defaultValue = "3") String limit) throws JsonProcessingException {
    log.info("START - TRANSACTION CONTROLLER - TRANSACTION HISTORY");
    TransactionRes res = transactionService.transactionHistory(token, offset, limit);
    log.info("FINISH - TRANSACTION CONTROLLER - TRANSACTION HISTORY");
    return ResponseEntity.ok(res);
  }
  
}
