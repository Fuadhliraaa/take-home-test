package com.assignment.test.service;

import com.assignment.test.dto.informationdto.InfoRes;

public interface InformationService {
  
  public InfoRes newGetBannerInfo(String token) throws RuntimeException;
  public InfoRes newGetAllServices(String token) throws RuntimeException;
  
}
