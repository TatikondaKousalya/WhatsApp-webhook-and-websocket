package com.chatapp.security.jwt;

import com.chatapp.data.entity.Role;
import com.chatapp.data.entity.User;
import com.chatapp.data.repository.RoleRepository;
import com.chatapp.exception.ResourceNotFoundException;
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

    @Value("${application.security.jwt.secret-key}")
    private String jwtSecret;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    private final RoleRepository roleRepository;

    /**
     * Generate JWT with default claims
     */
    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        if (user.getRoleId() != null) {

            Role role = roleRepository.findById(user.getRoleId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Role not found."));

            claims.put("role", role.getName());
        }

        return generateToken(claims, user);
    }

    /**
     * Generate JWT with additional claims
     */
    public String generateToken(Map<String, Object> claims, User user) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(
                        new Date(System.currentTimeMillis() + jwtExpiration)
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username(email)
     */
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration
     */
    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic claim extractor
     */
    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Validate JWT
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    /**
     * Check expiration
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Read all claims
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Decode Base64 secret
     */
    private Key getSigningKey() {

        byte[] key = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(key);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}