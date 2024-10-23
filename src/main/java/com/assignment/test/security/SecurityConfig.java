package com.assignment.test.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    
    http.authorizeHttpRequests((authz) -> authz
            .requestMatchers("/api/users/registration", "/api/users/login").permitAll()// Allow access to registration endpoint
            .anyRequest().authenticated()  // All other requests require authentication
        )
        .csrf((csrf) -> csrf.ignoringRequestMatchers("/api/users/registration", "/api/users/login")) // Disable CSRF for registration
        .httpBasic();
    
    return http.build();
  }

}
