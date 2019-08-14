package com.marketing.web.security.JWTAuthentication;

import com.marketing.web.security.JWTAuthToken.JWTAuthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public JWTAuthenticationFilter() {
        super("/api/**");
    }

    @Override
    public Authentication attemptAuthentication (HttpServletRequest httpServletRequest, HttpServletResponse
            httpServletResponse) throws AuthenticationException, IOException, ServletException {

        String header = httpServletRequest.getHeader("Authorization");


        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token missing or invalid");
        }

        String authenticationToken = header.substring(7);
        JWTAuthToken token = new JWTAuthToken(authenticationToken);
        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);

        chain.doFilter(request, response);
    }
}
