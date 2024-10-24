package com.assignment.test.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  
  private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  
  private final JwtService jwtService;
  
  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }
  
//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//    String authorizationHeader = request.getHeader("Authorization");
//
//    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//      String token = authorizationHeader.substring(7);
//      log.info("TOKEN =>>>>>>>> " + token);
//      String email = jwtService.extractEmail(token);
//
//      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//        if (jwtService.validateToken(token, email)) {
//          UsernamePasswordAuthenticationToken authentication =
//              new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
//          SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//      }
//    }
//    filterChain.doFilter(request, response);
//  }
  
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain ) throws ServletException, IOException {
    if (request.getServletPath().contains("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    jwt = authHeader.substring(7);
    userEmail = jwtService.extractUsername(jwt);
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//      var isTokenValid = tokenRepository.findByToken(jwt)
//          .map(t -> !t.isExpired() && !t.isRevoked())
//          .orElse(false);
      if (jwtService.isTokenValid(jwt, userEmail)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail, null, new ArrayList<>());
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }

}
