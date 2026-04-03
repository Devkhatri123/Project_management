package com.project_management.project_management.config.websocket;

import com.project_management.project_management.service.JwtService;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class CustomHandShakerHandler extends DefaultHandshakeHandler {

    private JwtService jwtService;

    public CustomHandShakerHandler(){

    }
    @Autowired
    public CustomHandShakerHandler(final JwtService jwtService){
        this.jwtService = jwtService;
    }

    @Override
    protected Principal determineUser(final ServerHttpRequest request, final WebSocketHandler webSocketHandler,
                                   Map<String, Object> attributes){
        if(request instanceof ServletServerHttpRequest servletRequest){
            Cookie[] cookies = servletRequest.getServletRequest().getCookies();
            String access_token = null;
            if(cookies != null){
                for (Cookie cookie : cookies){
                    if("access_token".equals(cookie.getName())){
                        access_token = cookie.getValue();
                        break;
                    }
                }
                if(access_token != null){
                  String user_id = jwtService.extractUserId(access_token);
                  attributes.put("access_token", access_token);
                  return new CustomPrincipal(user_id);
                }
            }
        }
       return null;
    }

}
