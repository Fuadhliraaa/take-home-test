package com.assignment.test.service;

import com.assignment.test.dto.LoginReq;
import com.assignment.test.dto.LoginRes;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface LoginService {
  
  public LoginRes userLogin(LoginReq req) throws JsonProcessingException;
  
}
