//package com.assignment.test.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.*;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http
//        .authorizeHttpRequests((requests) -> requests
//            .requestMatchers("/registration").permitAll()
//            .anyRequest().authenticated()
//        )
//        .formLogin((form) -> form
//            .loginPage("/login")
//            .permitAll()
//        )
//        .logout((logout) -> logout.permitAll());
//
//    return http.build();
//  }
//
//}
