package com.simupi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Global application configuration defining security and crypto beans.
 */
@Configuration
public class AppConfig {

    /**
     * BCrypt PasswordEncoder bean used for hashing and verifying passwords and UPI PINs.
     * Work factor 10 provides standard high-entropy collision resistance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
