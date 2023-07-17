package com.monie.xpress.auth_config.security.auth_utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtService {
    //This is the access token expiration time. it's an environment variable, and it's in seconds
     @Value("${access_expiration}")
    private long accessExpiration;
     //This is the refresh token expiration time. it's an environment variable, and it's in seconds
    @Value("${refresh_expiration}")
    private long refreshExpiration;

    private final Key key;	//This is the key used in signing the jwt token and it's autowired


    //this method extracts the email(username) from the jwt token, and it's used at the point of authorization
    public String extractUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //this method generates the refresh token for authentication
    //refresh token has a longer expiration than access token
    public String generateRefreshToken(String email) {
        return generateToken(new HashMap<>(), email, refreshExpiration);
    }

    //this method generates the access token for authentication
    //access token has a shorter expiration than refresh token
    public String generateAccessToken(Map<String, Object> claims, String email) {
        return generateToken(claims, email, accessExpiration);
    }

    private String generateToken(Map<String, Object> claims, String email, Long expiration) {
        final Date expiredAt = Date.from(Instant.now().plusSeconds(expiration));
        return Jwts.builder()
                .setIssuer("Hero")
                .setIssuedAt(Date.from(Instant.now()))
                .setClaims(claims)
                .setSubject(email)
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    //this method validates the generated token
    public Boolean isValid(String token) {
        try {
           final Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
           final Date expiration = claims.getExpiration();
            return expiration != null &&
                    expiration.after(Date.from(Instant.now()));
        } catch (JwtException e) {
            return false;
        }
    }
}
