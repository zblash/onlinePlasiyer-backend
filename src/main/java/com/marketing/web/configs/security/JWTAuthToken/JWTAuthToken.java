package com.marketing.web.configs.security.JWTAuthToken;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JWTAuthToken extends UsernamePasswordAuthenticationToken {

    private String token;

    public JWTAuthToken(String token) {
        super(null, null);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

}
