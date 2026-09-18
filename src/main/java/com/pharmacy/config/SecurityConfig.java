package com.pharmacy.config;

import com.pharmacy.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,"/api/product/search").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/product/*").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/product/categories/*").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/product/**").hasAnyRole("PHARMACIST", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/product/**").hasAnyRole("PHARMACIST", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/product/**").hasAnyRole("PHARMACIST", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/cart").hasAnyRole("CLIENT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/cart/merge").hasAnyRole("CLIENT", "ADMIN")

                        .requestMatchers("/api/user/**").hasRole("ADMIN")

                        .requestMatchers("/api/auth/*").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}

