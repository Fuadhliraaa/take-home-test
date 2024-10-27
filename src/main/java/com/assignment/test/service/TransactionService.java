package com.assignment.test.service;

import com.assignment.test.dto.trxdto.TransactionReq;
import com.assignment.test.dto.trxdto.TransactionRes;

public interface TransactionService {

  public TransactionRes newGetBalance(String token) throws RuntimeException;
  public TransactionRes newTopUpBalance(TransactionReq req, String token) throws RuntimeException;
  public TransactionRes newDoTransaction(TransactionReq req, String token) throws RuntimeException;
  public TransactionRes newTransactionHistory(String token, int offset, int limit) throws RuntimeException;

}
