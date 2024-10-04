package com.msp.everestFitness.everestFitness.config.security;

import com.msp.everestFitness.everestFitness.enumrated.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/verify-email",
                                "/api/auth/reset-form",
                                "/api/auth/google/**",
                                "/error",

                                // OpenAPI 3.x (Swagger UI v3)
                                "/v3/api-docs/**",
                                "/swagger-ui/**",

                                // Swagger UI v2 (Swagger 2.0)
                                "/v2/api-docs",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui.html",
                                "/webjars/**").permitAll()  //access Allowed without login

                        // Endpoints that do not require authentication only for get method
                        .requestMatchers(HttpMethod.GET, "/api/subcategory/",
                                "/api/subcategory/by-category",
                                "/api/category/",
                                "/api/shipping/info/",
                                "/api/product/",
                                "/api/product-ratings/",
                                "/api/payment/success",
                                "/api/payment/failed").permitAll() //access Allowed without login only for get method

                        // Endpoints that require ADMIN role
                        .requestMatchers("/api/subcategory/",
                                "/api/subcategory/",
                                "/api/category/", "/api/product/",
                                "/api/order-item/").hasRole("ADMIN")  // Only ADMIN can access

                        // Endpoints that require MEMBER or USER role
                        .requestMatchers("/api/shipping/info/", "/api/order/").hasAnyRole("MEMBER", "USER", "GUEST")  // Only MEMBER, GUEST, and USER can access

                        // Endpoints that require authentication
                        .requestMatchers("/test").authenticated()        // Require authentication for this endpoint
                        .anyRequest().authenticated()                    // Require authentication for all other requests
                )
                // OAuth2 login configuration for Google
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/api/auth/google") // Optional: Define a custom Google login page
//                        .defaultSuccessUrl("/api/auth/google-success", true)
//                )
                // Exception handling configuration
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(point)  // Use custom entry point for unauthorized access
                )

                // Configure stateless session management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Add custom JWT authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        // Build the security filter chain
        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
//    }
//
//    private ClientRegistration googleClientRegistration() {
//        return ClientRegistration.withRegistrationId("google")
//                .clientId("your-google-client-id")
//                .clientSecret("your-google-client-secret")
//                .scope("openid", "profile", "email")
//                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
//                .tokenUri("https://oauth2.googleapis.com/token")
//                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
//                .userNameAttributeName("sub")
//                .clientName("Google")
//                .redirectUri("{baseUrl}/login/oauth2/code/google")
//                .build();
//    }
}
