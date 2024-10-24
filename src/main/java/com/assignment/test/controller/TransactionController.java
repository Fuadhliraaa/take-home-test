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
  public ResponseEntity topUpBalance(@RequestBody TransactionReq req, @RequestHeader("Authorization") String token) throws JsonProcessingException {
    log.info("START - TRANSACTION CONTROLLER - TOP UP BALANCE");
    TransactionRes res = transactionService.topUpBalance(req, token);
    log.info("FINISH - TRANSACTION CONTROLLER - TOP UP BALANCE");
    return ResponseEntity.ok(res);
  }
  
}
