package com.marketing.web.configs;

import com.marketing.web.models.User;
import com.marketing.web.security.JWTAuthToken.JWTValidator;
import com.marketing.web.security.JWTAuthentication.JWTAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Component
public class UserInterceptor extends ChannelInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private JWTAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private JWTValidator jwtValidator;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String header = accessor.getFirstNativeHeader("Authorization");

            if (!header.isEmpty()) {
                log.info("Header auth token: " + header);
                String authenticationToken = header.substring(7);


                User user = jwtValidator.validate(authenticationToken);
                List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                        .commaSeparatedStringToAuthorityList(user.getRole().getName());
                user.setName(user.getUsername());
                Principal principal = new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);

                accessor.setUser(principal);
                log.info("Logged in user "+principal.toString());
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (Objects.nonNull(authentication))
                    log.info("Disconnected Auth : " + authentication.getName());
                else
                    log.info("Disconnected Sess : " + accessor.getSessionId());
            }
        }

        return message;
    }

}
