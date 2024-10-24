package com.assignment.test.controller;

import com.assignment.test.dto.information.InfoRes;
import com.assignment.test.service.InformationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
public class InformationController {
  
  private final Logger log = LoggerFactory.getLogger(InformationController.class);
  
  @Autowired
  private InformationService informationService;
  
  @GetMapping("/banner")
  public ResponseEntity getBannerInfo(@RequestHeader("Authorization") String token) throws JsonProcessingException {
    log.info("START - INFORMATION CONTROLLER - BANNER INFO");
    InfoRes res = informationService.getBannerInfo(token);
    log.info("FINISH - INFORMATION CONTROLLER - BANNER INFO");
    return ResponseEntity.ok(res);
  }
  
  @GetMapping("/services")
  public ResponseEntity getAllService(@RequestHeader("Authorization") String token) throws JsonProcessingException {
    log.info("START - INFORMATION CONTROLLER - GET ALL SERVICE");
    InfoRes res = informationService.getAllServices(token);
    log.info("FINISH - INFORMATION CONTROLLER - GET ALL SERVICE");
    return ResponseEntity.ok(res);
  }
  
}
