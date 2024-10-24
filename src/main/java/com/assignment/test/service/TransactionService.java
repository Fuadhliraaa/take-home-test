package com.assignment.test.service;

import com.assignment.test.dto.trxdto.TransactionReq;
import com.assignment.test.dto.trxdto.TransactionRes;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface TransactionService {

  public TransactionRes getBalance(String token) throws JsonProcessingException;
  public TransactionRes topUpBalance(TransactionReq req, String token) throws JsonProcessingException;

}
