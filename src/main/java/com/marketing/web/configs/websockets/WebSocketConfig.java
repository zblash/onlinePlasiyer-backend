package com.marketing.web.configs.websockets;

import com.marketing.web.enums.WebSocketType;
import com.marketing.web.services.websocket.WebSocketNotificationSenderService;
import com.marketing.web.services.websocket.WebSocketProductSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    WebSocketNotificationSenderService notificationSenderService;

    @Autowired
    WebSocketProductSenderService productSenderService;

    @Autowired
    private WebSocketJWTValidator webSocketJWTValidator;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry
                                                  webSocketHandlerRegistry) {

        webSocketHandlerRegistry.addHandler(createHandler(WebSocketType.NOTIFY),
                "/user/queue/notifications")
                .addHandler(createHandler(WebSocketType.PRDCT),"/queue/products/*")
                .setHandshakeHandler(new AuthenticationHandshakeHandler(webSocketJWTValidator, "setPrincipal"))
        .addInterceptors(new CustomHandshakeInterceptor());
    }

    public WebSocketHandler createHandler(WebSocketType type) {
        switch (type) {
            case NOTIFY:
                return new NotificationWebSocketHandler(notificationSenderService);
            case PRDCT:
                return new ProductsWebSocketHandler(productSenderService);
            default:
                return null;
        }
    }
}