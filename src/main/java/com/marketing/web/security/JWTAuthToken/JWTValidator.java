package com.marketing.web.security.JWTAuthToken;

import com.marketing.web.models.User;
import com.marketing.web.services.user.UserService;
import com.marketing.web.services.user.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JWTValidator {

    @Autowired
    private UserService userService;

    Logger logger = LoggerFactory.getLogger(JWTValidator.class);
    private String secret = "D6D317C8F7CEDC7B170B892FE9D3A8C4CD0861BE653203FB6D349C2478D92811";

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

