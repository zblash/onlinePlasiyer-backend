package com.marketing.web.security.JWTAuthentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTAuthEntryPoint implements AuthenticationEntryPoint {



    @Override
    public void commence(HttpServletRequest arg0, HttpServletResponse arg1, AuthenticationException arg2)
            throws IOException, ServletException {
        arg1.sendError(HttpServletResponse.SC_UNAUTHORIZED,arg2.getMessage());

    }
}