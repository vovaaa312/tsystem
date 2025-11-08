package com.tsystem.service;

import com.tsystem.model.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
@RequiredArgsConstructor

public class JwtService {

    private static String SECRET_KEY;

    @Value("${jwt.secret-key}")
    private void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    private static int JWT_EXPIRATION_TIME;

    @Value("${jwt.expiration-ms}")
    private void setJwtExpirationTime(int jwtExpirationTime) {
        JWT_EXPIRATION_TIME = jwtExpirationTime;
    }


    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public Date extractIssuedAt(String jwt) {
        return extractClaim(jwt, Claims::getIssuedAt);
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }


    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // to check if userDetails is instance of User
        if (!(userDetails instanceof User)) {
            throw new IllegalArgumentException("UserDetails must be instance of User");
        }
        User user = (User) userDetails;
        // check if userId is not null
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        claims.put("userId", user.getId());
        System.out.println("Generating token with claims: " + claims);
        return generateToken(claims, userDetails);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        //  int i = 7 * 24 * 60 * 60 * 1000;
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }



public boolean isTokenValid(String jwt, User user) {
    try {
        final String username = extractUsername(jwt);
        if (username == null || !username.equals(user.getUsername())) return false;
        if (isTokenExpired(jwt)) return false;

        Date iat = extractIssuedAt(jwt);
        if (iat != null && user.getPasswordChangedAt() != null
                && iat.toInstant().isBefore(user.getPasswordChangedAt().toInstant())) {
            return false;
        }
        return true;
    } catch (Exception e) {
        return false;
    }
}
    private boolean isTokenExpired(String jwt) {

        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }


    private Claims extractAllClaims(String jwt) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();

    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}