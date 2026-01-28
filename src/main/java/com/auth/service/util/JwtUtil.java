package com.auth.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String key;

    // default 1 hour in milliseconds
    @Value("${jwt.expiration:3600000}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String role){
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("test", "test claim")
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .issuedAt(new Date())
                .issuer("auth-service")
                .signWith(getSigningKey())
                .compact();
    }

    public Claims verifyAndGetAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer("auth-service")
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}