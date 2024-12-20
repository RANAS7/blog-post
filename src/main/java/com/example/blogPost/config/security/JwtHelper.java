package com.example.blogPost.config.security;

import com.example.blogPost.exceptions.ResourceNotFoundException;
import com.example.blogPost.model.Users;
import com.example.blogPost.repository.UsersRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;  // Import java.util.Date instead of java.sql.Date
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtHelper {

    public static final long JWT_TOKEN_VALIDITY = 10 * 24 * 60 * 60;  // Token validity duration in seconds

    @Value("${JWT_SECRET_KEY}")
    private String secret;  // Secret key

    @Autowired
    private UsersRepo usersRepo;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());  // Generate signing key from secret
    }

    // Retrieve username from JWT token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Retrieve expiration date from JWT token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Generic method to retrieve a specific claim from JWT token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Retrieve all claims from JWT token using the signing key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Generate a token for the user
    public String generateToken(UserDetails userDetails) {
        // Retrieve user by username (email in this case)
        Users users =usersRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the username: " + userDetails.getUsername()));


        // Create claims map with the user role
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", users.getUserType());  // Use "role" instead of "Role" for consistency
        return doGenerateToken(claims, userDetails.getUsername());
    }

    // Create the token by setting claims, expiration date, and signing with the key
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Validate the token with user details
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
