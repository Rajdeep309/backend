package com.crypto.PortfolioTracker.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final long TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000L;

    @Value("${app.jwt.secret}")
    private String secretKey;

    public String GenerateToken(Long id, String email) {

        return Jwts.builder()
                .setSubject(email)
                .claim("Id", id)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public Long validateToken(String token) {

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("Id", Long.class);

        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
