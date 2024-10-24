package com.assignment.test.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
//
//    http
//        .authorizeHttpRequests((authz) -> authz
//            .requestMatchers("/api/users/registration", "/api/users/login").permitAll() // Public paths
//            .anyRequest().authenticated()  // Require authentication for other paths
//        )
//        .csrf((csrf) -> csrf
//            .ignoringRequestMatchers("/api/users/registration", "/api/users/login").disable()  // Disable CSRF for public paths
//        )
//        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Disable basic authentication if using JWT
//        .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // Ensure stateless session
//
//    return http.build();
//  }
  
  private static final String[] WHITE_LIST_URL = {"/api/users/registration", "/api/users/login"};
  
  private final JwtAuthenticationFilter jwtAuthFilter;
//  private final AuthenticationProvider authenticationProvider;
  
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(req ->
            req.requestMatchers(WHITE_LIST_URL)
                .permitAll()
                .requestMatchers("/api/users/registration", "/api/users/login").permitAll()
                .anyRequest()
                .authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
//        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
  }

}
