package com.sathya.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtUtil {

    // ✅ Use same key as auth-service
    private static final String SECRET = "MySuperSecretKeyForJwtThatShouldBeLongEnough123456789"; // at least 32 bytes
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public static Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public static String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }
}
