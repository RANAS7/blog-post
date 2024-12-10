package com.example.blogPost.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint point;

    @Autowired
    private JwtAuthenticationFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (Cross-Site Request Forgery)
                .csrf(AbstractHttpConfigurer::disable)

                // Disable CORS (Cross-Origin Resource Sharing)
                .cors(AbstractHttpConfigurer::disable)

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Endpoints that do not require authentication
                        .requestMatchers("/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/verify-email",
                                "/api/auth/reset-form")
                        .permitAll()  // Access allowed without login

                        // Endpoints that require GET method and ADMIN role
                        .requestMatchers(HttpMethod.GET, "/api/posts/**", "/api/posts/comments/**", "/api/profile")
                        .permitAll()  // Only ADMIN can access these GET endpoints


                        .requestMatchers("/api/auth/users").hasRole("ADMIN")
                        // Endpoints that require USER role
                        .requestMatchers("/api/posts/**", "/api/posts/comments/**")
                        .hasAnyRole("USER")  // User can access these routes

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )

                // Exception handling configuration
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(point)  // Use custom entry point for unauthorized access
                )

                // Configure stateless session management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No session creation
                )

                // Add custom JWT authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        // Build and return the security filter chain
        return http.build();
    }

}
