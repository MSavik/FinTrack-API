package com.fintrack.fintrack_api.service;

import com.fintrack.fintrack_api.security.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String extractUsernameFromJwtToken(String token) {
        return extractClaimFromJwtToken(token, Claims::getSubject);
    }

    private <T> T extractClaimFromJwtToken(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaimsFromJwtToken(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaimsFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateJwtToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsernameFromJwtToken(token);
            return username.equals(userDetails.getUsername());
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }
}
