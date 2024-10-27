package com.assignment.test.dto.informationdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDto {
  
  private String service_code;
  private String service_name;
  private String service_icon;
  private BigDecimal service_tarif;
  
}
