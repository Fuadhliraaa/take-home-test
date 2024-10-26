package com.assignment.test.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.function.Function;

@Service
public class JWTUtils {
  
  @Value("${application.security.jwt.secret-key}")
  private String secretKey;
  
  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }
  
  public boolean validateToken(String token, String email) {
    String newToken = getTokenFromAuthorizationHeader(token);
    final String tokenEmail = extractEmail(newToken);
    return (tokenEmail.equals(email) && ! isTokenExpired(newToken));
  }
  
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }
  
  public String generateToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }
  
  public String getTokenFromAuthorizationHeader(String token) {
    
    if (token != null && token.startsWith("Bearer ")) {
      return token.substring(7);
    }
    
    return null;
  }
  
  public String getEmailFromPayload(String token) {
    DecodedJWT decodedJWT = JWT.decode(token);
    String email = decodedJWT.getClaim("email").asString();
    return email;
  }
  
  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
  
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }
  
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
  
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
  
}
