package com.assignment.test.controller;

import com.assignment.test.dto.swagger.UserRegisSwaggerReq;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SwaggerController {
  
  public ResponseEntity registrasiUser(@Valid @RequestBody UserRegisSwaggerReq req) {

  }
  
}
