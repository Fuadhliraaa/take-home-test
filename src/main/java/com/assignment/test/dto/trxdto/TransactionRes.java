package com.assignment.test.dto.trxdto;

import com.assignment.test.dto.BaseRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRes extends BaseRes {
  
  private DataDto data;
  
}
