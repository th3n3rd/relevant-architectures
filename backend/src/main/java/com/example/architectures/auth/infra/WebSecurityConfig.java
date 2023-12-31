package com.example.architectures.auth.infra;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    SecurityFilterChain actuatorApiSecurity(HttpSecurity http) throws Exception {
        return http
            .securityMatchers(config -> config.requestMatchers(EndpointRequest.toAnyEndpoint()))
            .authorizeHttpRequests(config -> config.anyRequest().permitAll())
            .build();
    }

    @Bean
    SecurityFilterChain webApiSecurity(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(config -> config.anyRequest().authenticated())
            .csrf(config -> config.disable())
            .oauth2ResourceServer(config -> config.jwt(
                jwt -> jwt.jwtAuthenticationConverter(ConsultantAuthToken::new)
            ))
            .build();
    }
}
