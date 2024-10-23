package com.assignment.test.service;

import com.assignment.test.dto.LoginReq;
import com.assignment.test.dto.LoginRes;
import com.assignment.test.dto.UserReq;
import com.assignment.test.dto.UserRes;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserService {

  public UserRes userRegistration(UserReq req) throws RuntimeException, JsonProcessingException;
  public LoginRes userLogin(LoginReq req) throws JsonProcessingException;

}
