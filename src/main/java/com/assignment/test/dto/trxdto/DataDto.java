package com.assignment.test.dto.trxdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataDto {
  
  private BigDecimal balance;
  private String invoice_number;
  private String service_code;
  private String service_name;
  private String transaction_type;
  private BigDecimal total_amount;
  private Timestamp created_on;
  
  private String offset;
  private String limit;
  private List<TransactionHistoryDto> records;
  
}
