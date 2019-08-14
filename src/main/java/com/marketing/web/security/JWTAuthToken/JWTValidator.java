package com.marketing.web.security.JWTAuthToken;

import com.marketing.web.models.User;
import com.marketing.web.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JWTValidator {

    @Autowired
    private UserService userService;

    Logger logger = LoggerFactory.getLogger(JWTValidator.class);
    private String secret = "cokkgizli";

    public User validate(String token) {

        User user = null;
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            user = userService.findById(Long.parseLong(body.get("userId").toString()));

        }
        catch (ExpiredJwtException | SignatureException e) {
            throw e;
        }

        return user;
    }
}

