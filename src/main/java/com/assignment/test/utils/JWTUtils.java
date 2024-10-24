package com.assignment.test.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

public class JWTUtils {
  
  public static String getTokenFromAuthorizationHeader(String token) {
    
    if (token != null && token.startsWith("Bearer ")) {
      return token.substring(7);
    }
    
    return null;
  }
  
  public static String getEmailFromPayload(String token) {
    DecodedJWT decodedJWT = JWT.decode(token);
    String email = decodedJWT.getClaim("email").asString();
    return email;
  }
  
}
