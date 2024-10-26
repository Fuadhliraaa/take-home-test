package com.assignment.test.service;

import com.assignment.test.dto.userdto.LoginReq;
import com.assignment.test.dto.userdto.LoginRes;
import com.assignment.test.dto.userdto.UserReq;
import com.assignment.test.dto.userdto.UserRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
  
  public UserRes newUserRegistration(UserReq req) throws RuntimeException;
  public LoginRes newUserLogin(LoginReq req) throws RuntimeException;
  public UserRes uploadImage(MultipartFile file, String token) throws RuntimeException;
  public UserRes newGetUserProfile(String token) throws RuntimeException;
  public UserRes newUpdateUserProfile(UserReq req, String token) throws RuntimeException;

}
