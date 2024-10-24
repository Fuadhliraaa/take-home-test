package com.assignment.test.utils;

import com.assignment.test.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class CommonUtils {
  
  private static final Logger log = LoggerFactory.getLogger(JwtService.class);
  
  public static String generateDynamicFileName(String originalFileName) {
    String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
    return UUID.randomUUID().toString().concat(".").concat(fileExtension); // Generates a random name with the same extension
  }
  
  public static String getAuthToken(String token) {
    return token.substring(7);
  }
  
  public static String keyGenerator() {
    final String character = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    final int length = 50;
    
    Random random = new SecureRandom();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int index = random.nextInt(character.length());
      builder.append(character.charAt(index));
    }
    
    log.info("SIGN KEY -> "+ builder.toString());
    
    return builder.toString();
  }
  
}
