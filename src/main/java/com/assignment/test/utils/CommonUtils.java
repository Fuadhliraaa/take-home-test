package com.assignment.test.utils;

import java.util.UUID;

public class CommonUtils {
  
  public static String generateDynamicFileName(String originalFileName) {
    String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
    return UUID.randomUUID().toString().concat(".").concat(fileExtension); // Generates a random name with the same extension
  }
  
}
