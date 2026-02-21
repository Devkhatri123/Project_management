package com.project_management.project_management.service;

import com.project_management.project_management.model.User;
import com.project_management.project_management.model.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
    @Value("${spring.jwt.secretKey}")
    private String SECRET_KEY;
    @Value("${spring.jwt.expirationMs}")
    private int jwt_expiry;

    public String generateJwt(User user){
        Map<String,Object> claims = new HashMap<>();
        // claims.put("hasSubscription", !user.getRole().equals("GUEST") ? true : false);
        return Jwts.builder()
                .subject(user.getId())
                .issuedAt(new Date())
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + jwt_expiry))
                .signWith(generateKey(),Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey generateKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }
    private Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String extractUserId(String token){
        return extractClaim(token, Claims::getSubject);
    }
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public boolean isTokenValid(String token, UserDetailsImpl user){
        String id = extractUserId(token);
        return (id.equals(user.getUser().getId()) && !isTokenExpired(token));
    }
}
