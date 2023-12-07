package com.example.architectures.common;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(config -> config.anyRequest().authenticated())
            .csrf(config -> config.disable())
            .oauth2ResourceServer(config -> config.jwt(withDefaults()))
            .build();
    }

}
