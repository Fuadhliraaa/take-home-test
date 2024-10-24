package com.assignment.test.dto.userdto;

import com.assignment.test.dto.BaseRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRes extends BaseRes {
  
  private Object data;
  
}
