package com.gigtasker.chatservice.config;

import org.gigtasker.common.security.GigTaskerSecurity;
import org.gigtasker.common.security.SecurityCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GigTaskerSecurity.class)
public class SecurityConfig {

    @Bean
    public SecurityCustomizer chatWebSockets() {
        return authorize -> authorize
                .requestMatchers("/ws/**").permitAll(); // Allow Handshake
    }
}
