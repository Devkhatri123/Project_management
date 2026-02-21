package com.project_management.project_management.jwt;

import com.project_management.project_management.model.UserDetailsImpl;
import com.project_management.project_management.service.JwtService;
import com.project_management.project_management.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailServiceImpl;

    @Autowired
    public JwtFilter(final JwtService jwtService, final UserDetailsServiceImpl userDetailService){
        this.jwtService = jwtService;
        this.userDetailServiceImpl = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String id = null;
        String token = null;
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
            token = authorizationHeader.substring(7);
        }
        if(token != null){
            try{
                id = jwtService.extractUserId(token);
            }catch (IllegalArgumentException e){
                // resolver.resolveException(request, response, null, e);
                log.error("IllegalArgumentException:", e);
                return;
            }catch (MalformedJwtException e){
                // resolver.resolveException(request, response, null, e);
                log.error("Token has been malformed: {}", e.getMessage());
                return;
            }catch (ExpiredJwtException e){
                // resolver.resolveException(request, response, null, e);
                log.error("Token has been expired: {}", e.getMessage());
                return;
            }catch (Exception e){
                // resolver.resolveException(request, response, null, e);
                log.error("Jwt Exception:", e);
                return;
            }
            if(id != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetailsImpl user = null;
                try{
                    user = (UserDetailsImpl) userDetailServiceImpl.loadUserByUsername(id);
                }catch (UsernameNotFoundException e){
                    log.error("username not found");
                }
                boolean isTokenValid = jwtService.isTokenValid(token,user);
                if(isTokenValid){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
