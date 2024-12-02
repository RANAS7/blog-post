package com.msp.everestFitness.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtHelper jwtHelper;


    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Authorization
        String requestHeader = request.getHeader("Authorization");

        String username = null;
        String token = null;


        String requestUri = request.getRequestURI();

        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            token = requestHeader.substring(7);
            try {
                username = this.jwtHelper.getUsernameFromToken(token);
            } catch (ExpiredJwtException ex) {
                logger.info("JWT token has expired.", ex);
                throw new ExpiredJwtException(ex.getHeader(), ex.getClaims(), "JWT token has expired");
            } catch (MalformedJwtException ex) {
                logger.info("Malformed JWT token.", ex);
                throw new MalformedJwtException("Invalid JWT token format.");
            } catch (IllegalArgumentException ex) {
                logger.info("Illegal argument exception.", ex);
                throw new IllegalArgumentException("Invalid input provided.");
            } catch (Exception ex) {
                logger.info("An unexpected error occurred.", ex);
                throw new RuntimeException("An unexpected error occurred. Please try again later.");
            }
        } else {
            // Log and throw the invalid header value exception only if the path requires authentication
            logger.info("The Request URI is permitted without authentication: " + requestUri);

        }

        // If username is not null and SecurityContextHolder does not contain authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Fetch user details using the username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);

            if (validateToken) {
                // Set the authentication in the security context
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.info("Token validation failed.");
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

}