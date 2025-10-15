package com.dao.cookbook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.dao.cookbook.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(@Lazy UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // Bean PasswordEncoder để dùng trong UserService
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Bean JwtAuthenticationFilter riêng
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userService);
    }

    // Bean SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints - public
                .requestMatchers("/api/auth/**").permitAll()
                // Recipe endpoints - GET methods are public, others require authentication
                .requestMatchers("/api/recipes").permitAll()
                .requestMatchers("/api/recipes/{id}").permitAll()
                .requestMatchers("/api/recipes/user/{userId}").permitAll()
                .requestMatchers("/api/recipes/search").permitAll()
                .requestMatchers("/api/recipes/my-recipes").authenticated()
                // User endpoints - require authentication
                .requestMatchers("/api/users/**").authenticated()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Thêm filter JWT trước BasicAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }
}
