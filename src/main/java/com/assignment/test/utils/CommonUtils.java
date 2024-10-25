package com.assignment.test.utils;

import com.assignment.test.dto.trxdto.DataDto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonUtils {
  
  public static String generateDynamicFileName(String originalFileName) {
    String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
    return UUID.randomUUID().toString().concat(".").concat(fileExtension); // Generates a random name with the same extension
  }
  
  public static String generateInvoceNo() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
    String currentDate = dateFormat.format(new Date());
    String randomNum = String.valueOf(Math.floor(Math.random() * 10));
    return "INV" + currentDate + "-" + randomNum;
  }
  
  public static Timestamp getCurrentTimestamp() {
    return new Timestamp(System.currentTimeMillis());
  }
  
}
