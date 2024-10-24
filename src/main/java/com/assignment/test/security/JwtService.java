package com.assignment.test.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
  
  private static final Logger log = LoggerFactory.getLogger(JwtService.class);
  
  @Value("${jwt.secret}")
  private String secretKey;
  
  private static Key secret = MacProvider.generateKey();
  
//  // Extract the username (subject) from the token
//  public String extractEmail(String token) {
//    return extractClaim(token, Claims::getSubject);
//  }
//
//  // Extract any claims from the token
//  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//    final Claims claims = extractAllClaims(token);
//    return claimsResolver.apply(claims);
//  }
//
//  private Claims extractAllClaims(String token) {
//    log.info("SIGN KEY FROM JWT UTIL extract all claims -> "+ getSignKey());
//    return Jwts.parserBuilder()
//        .setSigningKey(getSignKey())
//        .build()
//        .parseClaimsJws(token)
//        .getBody();
//  }
//
//  // Generate a token for a user
//  public String generateToken(String email) {
//    Map<String, Object> claims = new HashMap<>( );
//    return createToken(claims, email);
//  }
//
//  private String createToken(Map<String, Object> claims, String subject) {
//    return Jwts.builder()
//        .setClaims(claims)
//        .setSubject(subject)
//        .setIssuedAt(new Date(System.currentTimeMillis()))
//        .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000))  // Token valid for 10 hours
//        .signWith(getSignKey(), SignatureAlgorithm.HS256)
//        .compact();
//  }
//
//  // Validate the token against the username and check expiration
//  public boolean validateToken(String token, String email) {
//    final String tokenEmail = extractEmail(token);
//    return (tokenEmail.equals(email) && !isTokenExpired(token));
//  }
//
//  private boolean isTokenExpired(String token) {
//    return extractExpiration(token).before(new Date());
//  }
//
//  private Date extractExpiration(String token) {
//    return extractClaim(token, Claims::getExpiration);
//  }
//
//  private Key getSignKey() {
//    byte[] keyBytes = Decoders.BASE64.decode(signKey);
//    return Keys.hmacShaKeyFor(keyBytes);
//  }
  
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }
  
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }
  
  public String generateToken(String email) {
    return generateToken(new HashMap<>(), email);
  }
  
  public String generateToken(
      Map<String, Object> extraClaims,
      String email
  ) {
    long jwtExpiration = 12 * 60 * 60 * 1000;
    return buildToken(extraClaims, email, jwtExpiration);
  }
  
//  public String generateRefreshToken(
//     String email
//  ) {
//    return buildToken(new HashMap<>(), email, refreshExpiration);
//  }
  
  private String buildToken(
      Map<String, Object> extraClaims,
      String email,
      long expiration
  ) {
    return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }
  
  public boolean isTokenValid(String token, String email) {
    final String emailToken = extractUsername(token);
    return (emailToken.equals(email)) && !isTokenExpired(token);
  }
  
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }
  
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
  
  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(secret.getEncoded())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
  
  private Key getSignInKey() {
    byte[] keyBytes = secret.getEncoded();
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
