package com.assignment.test.dto.swagger;

import com.assignment.test.dto.BaseRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisSwaggerRes extends BaseRes {

    private SwaggerDataDto data;

}
