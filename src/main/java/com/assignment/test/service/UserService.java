package com.assignment.test.service;

import com.assignment.test.dto.LoginReq;
import com.assignment.test.dto.LoginRes;
import com.assignment.test.dto.UserReq;
import com.assignment.test.dto.UserRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  public UserRes userRegistration(UserReq req) throws RuntimeException, JsonProcessingException;
  public LoginRes userLogin(LoginReq req) throws JsonProcessingException;
  public UserRes updloadImage(MultipartFile file, String token) throws JsonProcessingException;
  public UserRes getUserProfile(String token) throws JsonProcessingException;

}
