package com.marketing.web.configs.websockets;

import com.marketing.web.configs.constants.ApplicationContstants;
import com.marketing.web.models.User;
import com.marketing.web.configs.security.JWTAuthToken.JWTGenerator;
import com.marketing.web.services.user.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebSocketJWTValidator {

    @Autowired
    private UserService userService;

    public Principal setPrincipal(ServerHttpRequest request) {
        String url = request.getURI().toString();
        Pattern p = Pattern.compile("[&?]token=([^&\\r\\n]*)");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            Claims body = (Claims) JWTGenerator.validate(matcher.group(1), ApplicationContstants.JWT_SECRET);
            User user = userService.findById((body.get("userId").toString()));
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList(user.getRole().getName());
            user.setName(user.getUsername());
            return new UsernamePasswordAuthenticationToken(user.getName(), null, grantedAuthorities);
        }
        return null;
    }

}
