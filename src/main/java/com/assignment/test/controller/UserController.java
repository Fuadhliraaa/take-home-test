package com.assignment.test.controller;

import com.assignment.test.dto.userdto.LoginReq;
import com.assignment.test.dto.userdto.LoginRes;
import com.assignment.test.dto.userdto.UserReq;
import com.assignment.test.dto.userdto.UserRes;
import com.assignment.test.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
  
  @PostMapping("/login")
  public ResponseEntity userLogin(@RequestBody LoginReq req) throws JsonProcessingException {
    log.info("START - USER CONTROLLER - LOGIN USER");
    LoginRes res = userService.userLogin(req);
    log.info("FINISH - USER CONTROLLER - LOGIN USER");
    return ResponseEntity.ok(res);
  }
  
  @PutMapping("/profile/image")
  public ResponseEntity uploadImageProfile(@RequestParam MultipartFile file,
                                           @RequestHeader("Authorization") String token) throws JsonProcessingException {
    log.info("START - USER CONTROLLER - UPLOAD IMAGE");
    UserRes res = userService.updloadImage(file, token);
    log.info("START - USER CONTROLLER - UPLOAD IMAGE");
    return ResponseEntity.ok(res);
  }
  
  @GetMapping("/profile")
  public ResponseEntity getUserProfile(@RequestHeader("Authorization") String token) throws JsonProcessingException {
    log.info("START - USER CONTROLLER - USER PROFILE");
    UserRes res = userService.getUserProfile(token);
    log.info("FINISH - USER CONTROLLER - USER PROFILE");
    return ResponseEntity.ok(res);
  }
  
  @PutMapping("/profile/update")
  public ResponseEntity updateUserProfile(@RequestBody UserReq req, @RequestHeader("Authorization") String token) throws JsonProcessingException {
    log.info("START - USER CONTROLLER - UPDATE USER PROFILE");
    UserRes res = userService.updateUserProfile(req, token);
    log.info("FINISH - USER CONTROLLER - UPDATE USER PROFILE");
    return ResponseEntity.ok(res);
  }
  
}
