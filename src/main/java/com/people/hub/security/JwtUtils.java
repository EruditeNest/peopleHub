package com.people.hub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    public String createToken(String email, Long roleId, Long userId, boolean rememberMe) {
        Date expiry = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
        if (rememberMe) {
            //  token Expiry will be set to 15 days     Milli * Sec * Min * Hour * Days
            expiry = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 15);
        }
        Claims claims = Jwts.claims()
            .subject(email)
            .add("userId", userId)
            .add("roleId", roleId)
            .build();
        return generateToken(claims,expiry);
    }

    public String OtpToken(String email) {
        Date expiry = new Date(System.currentTimeMillis() + 1000 * 60 * 10);
        Claims claims = Jwts.claims()
            .subject(email)
            .build();
        return generateToken(claims,expiry);
    }

    public String generateToken(Claims claims, Date expiry) {
        log.info("Trying to generate token");
        Date issues = new Date(System.currentTimeMillis());
        return Jwts.builder()
            .claims(claims)
            .issuedAt(issues)
            .expiration(expiry)
            .signWith(getSecretKey())
            .compact();
    }

    public Map<String, String> getFromToken(String token) {
        Claims claims = extractAllClaims(token.substring(7));
        Map<String, String> claimsMap = new HashMap<>();
        claimsMap.put("userId", String.valueOf(claims.get("userId")));
        claimsMap.put("roleId", String.valueOf(claims.get("roleId")));
        claimsMap.put("email", String.valueOf(claims.getSubject()));
        return claimsMap;
    }


    public SecretKey getSecretKey() {
        byte[] secretBytes = secret.getBytes();
        return new SecretKeySpec(secretBytes, 0, secretBytes.length, "HmacSHA256");
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractRoleId(String token) {
        return extractClaim(token, claims -> claims.get("roleId", Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimresolver) {
        final Claims claims = extractAllClaims(token);
        return claimresolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean validateToken(String jwt, MyUserDetail userDetails) {
        return userDetails.getEmail().equals(extractUsername(jwt));
    }
}
