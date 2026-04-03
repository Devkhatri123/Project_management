package com.project_management.project_management.config.websocket;

import com.project_management.project_management.exception.Token.TokenExpired;
import com.project_management.project_management.service.JwtService;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomWebSocketInterceptor implements ChannelInterceptor {
    private JwtService jwtService;

    public CustomWebSocketInterceptor(){

    }
    @Autowired
    public CustomWebSocketInterceptor(final JwtService jwtService){
        this.jwtService = jwtService;
    }
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(StompCommand.SEND.equals(accessor.getCommand())){
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if(sessionAttributes != null && sessionAttributes.containsKey("access_token")) {
                String access_token = (String) sessionAttributes.get("access_token");
                boolean isAccessTokenExpired = jwtService.isTokenExpired(access_token);
                if(isAccessTokenExpired){
                    throw new MessageDeliveryException("ACCESS_TOKEN_EXPIRED");
                }
            } else {
                throw new MessageDeliveryException("JWT_TOKEN_MISSING. LOGIN AGAIN");
            }
        }
        return message;
    }
}
