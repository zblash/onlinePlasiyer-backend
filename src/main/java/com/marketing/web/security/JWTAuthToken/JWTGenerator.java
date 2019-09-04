package com.marketing.web.security.JWTAuthToken;

import com.marketing.web.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTGenerator {
    public static String generate(User jwtUser) {

        Claims claims = Jwts.claims()
                .setSubject(jwtUser.getUsername());
        claims.put("role", jwtUser.getRole().getName());
        claims.put("userId", jwtUser.getId());

        String j = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, "D6D317C8F7CEDC7B170B892FE9D3A8C4CD0861BE653203FB6D349C2478D92811")
                .setExpiration(new Date(System.currentTimeMillis() + 86_400_000))
                .compact();
        return j;
    }
}