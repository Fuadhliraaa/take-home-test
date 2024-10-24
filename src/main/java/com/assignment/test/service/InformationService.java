package com.assignment.test.service;

import com.assignment.test.dto.informationdto.InfoRes;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface InformationService {
  
  public InfoRes getBannerInfo(String token) throws JsonProcessingException;
  public InfoRes getAllServices(String token) throws JsonProcessingException;
  
}
