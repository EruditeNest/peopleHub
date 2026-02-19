package com.people.hub.security;

import com.people.hub.core.common.exception.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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

    public String createToken(String email, Long userId) {
        long expirationTime = 1000L * 60 * 15; // 15 min
        Date expiry = new Date(System.currentTimeMillis() + expirationTime);

        Claims claims = Jwts.claims()
            .subject(email)
            .add("userId", userId)
            .build();
        return generateToken(claims,expiry);
    }

    public String OtpToken(String email, Long userId, String uuid) {
        Date expiry = new Date(System.currentTimeMillis() + 1000 * 60 * 10);
        Claims claims = Jwts.claims()
            .subject(email)
            .add("userId", userId)
            .add("tokenType", "RESET_PASSWORD")
            .add("uuid", uuid)
            .build();
        return generateToken(claims, expiry);
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
        try{
            Claims claims = extractAllClaims(token.substring(7));
            Map<String, String> claimsMap = new HashMap<>();
            claimsMap.put("userId", String.valueOf(claims.get("userId")));
            claimsMap.put("email", String.valueOf(claims.getSubject()));
            return claimsMap;
        } catch (Exception e) {
            throw new InvalidJwtException("Invalid Token", e);
        }
    }

    public Map<String, String> getFromOtpToken(String token) {
        try{
            Claims claims = extractAllClaims(token.substring(7));
            Map<String, String> claimsMap = new HashMap<>();
            claimsMap.put("userId", String.valueOf(claims.get("userId")));
            claimsMap.put("uuid", String.valueOf(claims.get("uuid")));
            claimsMap.put("tokenType", String.valueOf(claims.get("tokenType")));
            claimsMap.put("email", String.valueOf(claims.getSubject()));
            return claimsMap;
        } catch (Exception e) {
            throw new InvalidJwtException("Invalid Token", e);
        }
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
        try{
            return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "Token expired", e);
        } catch (JwtException e) {
            throw new InvalidJwtException("Invalid token", e);
        }
    }

    public boolean validateToken(String jwt, MyUserDetail userDetails) {
        return userDetails.getEmail().equals(extractUsername(jwt));
    }
}
