package com.assignment.test.service;

import com.assignment.test.constant.BaseURLConstant;
import com.assignment.test.constant.CommonConstant;
import com.assignment.test.constant.QueryConstant;
import com.assignment.test.dto.UserReq;
import com.assignment.test.dto.UserRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService{

  private final Logger log = LoggerFactory.getLogger(UserService.class);
  private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private EntityManager entityManager;

  @Transactional
  public UserRes userRegistration(UserReq req) throws RuntimeException, JsonProcessingException {
    log.info("START - USER SERVICE - USER REGISTRATION");
    UserRes res = new UserRes();

    try {
      String BASE_URL_REGIS = BaseURLConstant.SWAGGER_BASE_URL.concat("/registration");
      ResponseEntity<UserRes> responseEntity = restTemplate.postForEntity(BASE_URL_REGIS, req, UserRes.class);

      res = responseEntity.getBody();

      entityManager.createNativeQuery(QueryConstant.QUERY_SAVE_USER)
          .setParameter("id", UUID.randomUUID().toString().replace("-", ""))
          .setParameter("email", req.getEmail())
          .setParameter("first_nm", req.getFirst_name())
          .setParameter("last_nm", req.getLast_name())
          .setParameter("password", req.getPassword())
          .executeUpdate();

    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400) {
        String errorResponse = e.getResponseBodyAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        res = objectMapper.readValue(errorResponse, UserRes.class);

      }
    }

    log.info("END - USER SERVICE - USER REGISTRATION");
    return res;
  }

  @Override
  public UserRes userLogin(UserReq req) throws JsonProcessingException {
    return null;
  }

}
