package com.pharmacy.config;

import com.pharmacy.constants.ApiEndpointConstants;
import com.pharmacy.model.UserRole;
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
                        .requestMatchers(HttpMethod.GET, ApiEndpointConstants.PRODUCT_SEARCH_PATH).permitAll()

                        .requestMatchers(HttpMethod.GET, ApiEndpointConstants.PRODUCT_BY_ID_PATH).permitAll()
                        .requestMatchers(HttpMethod.GET, ApiEndpointConstants.PRODUCT_CATEGORIES_PATH).permitAll()

                        .requestMatchers(HttpMethod.POST, ApiEndpointConstants.PRODUCT_ALL_PATHS)
                        .hasAnyRole(UserRole.PHARMACIST.name(), UserRole.ADMIN.name())

                        .requestMatchers(HttpMethod.PUT, ApiEndpointConstants.PRODUCT_ALL_PATHS)
                        .hasAnyRole(UserRole.PHARMACIST.name(), UserRole.ADMIN.name())

                        .requestMatchers(HttpMethod.DELETE, ApiEndpointConstants.PRODUCT_ALL_PATHS)
                        .hasAnyRole(UserRole.PHARMACIST.name(), UserRole.ADMIN.name())

                        .requestMatchers(HttpMethod.GET, ApiEndpointConstants.CART_ALL_PATHS)
                        .hasAnyRole(UserRole.CLIENT.name(), UserRole.ADMIN.name())

                        .requestMatchers(HttpMethod.GET, ApiEndpointConstants.ORDERS)
                        .hasAnyRole(UserRole.CLIENT.name(), UserRole.ADMIN.name())

                        .requestMatchers(HttpMethod.POST, ApiEndpointConstants.ORDERS)
                        .hasAnyRole(UserRole.CLIENT.name(), UserRole.ADMIN.name())

                        .requestMatchers(ApiEndpointConstants.USER_ALL_PATHS).hasRole(UserRole.ADMIN.name())

                        .requestMatchers(HttpMethod.GET, ApiEndpointConstants.ORDERS_ADMIN_PATH)
                        .hasRole(UserRole.ADMIN.name())

                        .requestMatchers(HttpMethod.PUT, ApiEndpointConstants.ORDERS_ALL_PATHS)
                        .hasRole(UserRole.ADMIN.name())

                        .requestMatchers(ApiEndpointConstants.AUTH_ALL_PATHS).permitAll()
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

