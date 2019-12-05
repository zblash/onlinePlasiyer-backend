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
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JWTValidator {

    @Autowired
    private UserService userService;

    Logger logger = LoggerFactory.getLogger(JWTValidator.class);
    private String secret = "D6D317C8F7CEDC7B170B892FE9D3A8C4CD0861BE653203FB6D349C2478D92811";

    public User validate(String token) {
        User user;
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

