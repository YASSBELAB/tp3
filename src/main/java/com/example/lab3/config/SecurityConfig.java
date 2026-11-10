
package com.example.lab3.config;

import com.example.lab3.security.JwtAuthenticationFilter;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration cfg = new CorsConfiguration();

                    cfg.setAllowedOrigins(List.of(
                            "http://localhost:3000",
                            "http://127.0.0.1:5500",
                            "http://localhost:5173"
                    ));

                    cfg.setAllowedMethods(List.of(
                            "GET", "POST", "PUT", "DELETE", "OPTIONS"
                    ));

                    cfg.setAllowedHeaders(List.of(
                            "Authorization", "Content-Type"
                    ));

                    cfg.setAllowCredentials(true);
                    return cfg;
                }))

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(
                                        HttpStatus.UNAUTHORIZED.value(),
                                        "Unauthorized"
                                )
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(
                                        HttpStatus.FORBIDDEN.value(),
                                        "Forbidden"
                                )
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()

                        // Products: USER, MANAGER and ADMIN
                        .requestMatchers("/api/products/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")

                        // Categories: GET for USER, MANAGER and ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/categories/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")

                        // Other category operations: ADMIN only
                        .requestMatchers("/api/categories/**")
                        .hasRole("ADMIN")

                        // Orders: USER, MANAGER and ADMIN
                        .requestMatchers("/api/orders/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }
}
