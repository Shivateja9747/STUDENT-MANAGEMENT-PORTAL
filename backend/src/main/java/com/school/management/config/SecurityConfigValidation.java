package com.school.management.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SecurityConfigValidation {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfigValidation.class);

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @PostConstruct
    public void validate() {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalStateException("FATAL: JWT_SECRET environment variable/property is missing!");
        }

        List<String> defaultPasswords = Arrays.asList("", "root", "password", "123456", "edutrack_password", "admin");
        if (dbPassword != null && defaultPasswords.contains(dbPassword.toLowerCase().trim())) {
            logger.warn("=========================================================================");
            logger.warn("SECURITY WARNING: DB_PASSWORD is using a default or insecure value!");
            logger.warn("=========================================================================");
        }
    }
}
