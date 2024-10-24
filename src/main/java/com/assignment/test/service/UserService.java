package com.assignment.test.service;

import com.assignment.test.dto.user.LoginReq;
import com.assignment.test.dto.user.LoginRes;
import com.assignment.test.dto.user.UserReq;
import com.assignment.test.dto.user.UserRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  public UserRes userRegistration(UserReq req) throws RuntimeException, JsonProcessingException;
  public LoginRes userLogin(LoginReq req) throws JsonProcessingException;
  public UserRes updloadImage(MultipartFile file, String token) throws JsonProcessingException;
  public UserRes getUserProfile(String token) throws JsonProcessingException;
  public UserRes updateUserProfile(UserReq req, String token) throws JsonProcessingException;

}
