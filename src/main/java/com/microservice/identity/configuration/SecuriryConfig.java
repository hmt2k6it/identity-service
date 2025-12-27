package com.microservice.identity.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecuriryConfig {
    private static final String[] PUBLIC_ENDPOINTS = { "/users", "/auth/login", "/auth/introspect" };

    @Autowired
    JwtCustomDecoder jwtCustomDecoder;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll().anyRequest().authenticated());
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtCustomDecoder)).authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
        httpSecurity.csrf(csrf -> csrf.disable());
        return httpSecurity.build();
    }
}
