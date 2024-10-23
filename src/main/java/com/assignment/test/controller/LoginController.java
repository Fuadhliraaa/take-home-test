package com.assignment.test.controller;

import com.assignment.test.dto.LoginReq;
import com.assignment.test.dto.LoginRes;
import com.assignment.test.service.LoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users-auth")
public class LoginController {
  
  private final Logger log = LoggerFactory.getLogger(LoginController.class);
  
  @Autowired
  private LoginService loginService;
  
  @PostMapping("/login")
  public ResponseEntity userLogin(@RequestBody LoginReq req) throws JsonProcessingException {
    log.info("START - LOGIN CONTROLLER - LOGIN USER");
    LoginRes res = loginService.userLogin(req);
    log.info("FINISH - LOGIN CONTROLLER - LOGIN USER");
    return ResponseEntity.ok(res);
  }
  
}
