package com.marketing.web.security.JWTAuthentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.marketing.web.security.JWTAuthToken.JWTAuthToken;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.marketing.web.errors.HttpMessage;

public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public JWTAuthenticationFilter() {
        super("/api/**");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse
            httpServletResponse) throws AuthenticationException, IOException, ServletException {

        String header = httpServletRequest.getHeader("Authorization");


        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token missing or invalid");
        }

        String authenticationToken = header.substring(7);
        JWTAuthToken token = new JWTAuthToken(authenticationToken);
        Authentication authentication = null;
        try {
            authentication = getAuthenticationManager().authenticate(token);
        } catch (ExpiredJwtException | SignatureException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, httpServletResponse, e, httpServletRequest.getRequestURL().toString());
        } catch (RuntimeException e) {
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, httpServletResponse, e, httpServletRequest.getRequestURL().toString());
        }

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex, String path) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        HttpMessage error = new HttpMessage(status);
        error.setMessage(ex.getMessage());
        error.setPath(path);
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
