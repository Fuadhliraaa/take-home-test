package com.assignment.test.service;

import com.assignment.test.dto.UserReq;
import com.assignment.test.dto.UserRes;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserService {

  public UserRes userRegistration(UserReq req) throws RuntimeException, JsonProcessingException;
  public UserRes userLogin(UserReq req) throws JsonProcessingException;

}
