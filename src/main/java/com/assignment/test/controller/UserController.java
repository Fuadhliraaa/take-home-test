package com.assignment.test.controller;

import com.assignment.test.dto.UserReq;
import com.assignment.test.dto.UserRes;
import com.assignment.test.service.UserService;
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
@RequestMapping("/api/users")
public class UserController {

  private final Logger log = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @PostMapping("/registration")
  public ResponseEntity userRegistration(@RequestBody UserReq req) throws JsonProcessingException {
    log.info("START - USER CONTROLLER - USER REGISTRATION");
    UserRes res = userService.userRegistration(req);
    log.info("FINISH - USER CONTROLLER - USER REGISTRATION");
    return ResponseEntity.ok(res);
  }
  
}
