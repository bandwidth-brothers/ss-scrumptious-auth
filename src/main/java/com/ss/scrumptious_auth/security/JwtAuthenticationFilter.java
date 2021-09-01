package com.ss.scrumptious_auth.security;


import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ss.scrumptious_auth.dto.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.scrumptious_auth.entity.User;

import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final SecurityConstants securityConstants;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, SecurityConstants securityConstants) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.securityConstants = securityConstants;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        LoginViewModel credentials = null;
        try {
            credentials = objectMapper.readValue(request.getInputStream(), LoginViewModel.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    credentials.getUsername(), credentials.getPassword(), new ArrayList<>());
            Authentication auth = authenticationManager.authenticate(authenticationToken);
            return auth;
        } catch (Exception e) {
            e.printStackTrace();

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(null, null, null));
        }
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();

        String token = JWT.create().withSubject(user.getUsername()).withExpiresAt(securityConstants.getExpirationDate())
                .sign(Algorithm.HMAC512(securityConstants.getSECRET().getBytes()));

        String headerVal = securityConstants.getTOKEN_PREFIX() + token;
        response.addHeader(securityConstants.getHEADER_STRING(), headerVal);
        String respBody = objectMapper.writeValueAsString(new AuthResponse(user.getUserId(), headerVal, securityConstants.getExpirationDate()));
        response.getWriter().write(respBody);
    }

    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
        logger.debug("auth failed");
    }
}
