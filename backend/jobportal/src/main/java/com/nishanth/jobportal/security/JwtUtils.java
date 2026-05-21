package com.nishanth.jobportal.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {

    // POLISHED: Production-grade asynchronous SLF4J logging
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jobportal.jwt.secret}")
    private String secretString;

    @Value("${jobportal.jwt.expiration}")
    private long jwtExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        // Safe key conversion using v0.11.x requirements
        this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
        log.info("JWT Utility Component initialized successfully with stable cryptographic signature key.");
    }

    /**
     * Generates a token using syntax fully compatible with JJWT 0.11.x
     */
    public String generateToken(String email, String role) {
        long currentMillis = System.currentTimeMillis();
        
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date(currentMillis))
                .setExpiration(new Date(currentMillis + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Reusable helper to parse claims using the stable parserBuilder() engine
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

    /**
     * Validates incoming signatures safely with error shielding
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException | 
                 io.jsonwebtoken.MalformedJwtException | 
                 io.jsonwebtoken.security.SignatureException | 
                 IllegalArgumentException e) {
            log.warn("Inbound JWT Validation Drop Event: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Unexpected Runtime Error during token evaluation: {}", e.getMessage());
        }
        return false;
    }
}