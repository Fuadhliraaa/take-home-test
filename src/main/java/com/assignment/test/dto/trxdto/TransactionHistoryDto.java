package com.assignment.test.dto.trxdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDto {

  private String invoice_number;
  private String transaction_type;
  private String description;
  private BigDecimal total_amount;
  private Timestamp created_on;

}
