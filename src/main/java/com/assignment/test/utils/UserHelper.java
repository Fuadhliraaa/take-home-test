package com.assignment.test.utils;

import java.util.UUID;

public class UserHelper {
    
    public static String generateUUID() {
      return UUID.randomUUID().toString().replace("-", "");
    }

}
