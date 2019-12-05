package com.marketing.web.configs.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;
import java.util.Objects;

@Component
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        String path = serverHttpRequest.getURI().getPath();
        if (path.contains("/queue/products/")) {
            String stateId = path.substring(path.lastIndexOf("/") + 1);
            attributes.put("stateId", stateId);

        }
        return super.beforeHandshake(serverHttpRequest,serverHttpResponse,webSocketHandler,attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception ex) {
        super.afterHandshake(serverHttpRequest, serverHttpResponse, webSocketHandler, ex);
    }
}
