package com.assignment.test.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.security.KeyStore;
import java.util.Date;

public class JWTUtils {
  
//  public static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//
//  public static String generateToken(String email) {
//
//    long currentTimeMillis = System.currentTimeMillis();
//    Date now = new Date(currentTimeMillis);
//    Date expiryDate = new Date(currentTimeMillis + 12 * 60 * 60 * 1000);
//
//    return Jwts.builder()
//        .setSubject(email)
//        .setIssuedAt(now)
//        .setExpiration(expiryDate)
//        .signWith(SECRET_KEY)
//        .compact();
//
//  }
//
//  public String extractEmail(String token) {
//    Claims claims = Jwts.parser()
//        .setSigningKey(SECRET_KEY)
//        .parseClaimsJws(token)
//        .getBody();
//    return claims.getSubject();
//  }
  
}
