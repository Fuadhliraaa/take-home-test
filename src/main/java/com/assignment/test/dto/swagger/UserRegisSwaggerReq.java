package com.assignment.test.dto.swagger;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisSwaggerReq {

    @Email(message = "Email harus dengan format yang benar")
    @NotEmpty(message = "Email harus diisi")
    private String email;
    
    @NotEmpty(message = "Nama awal harus diisi")
    private String firstName;
    
    private String lastname;
    
    @Size(min = 8, message = "Password harus terdiri dari 8 karakter")
    @NotEmpty(message = "Password harus diisi")
    private String password;

}
