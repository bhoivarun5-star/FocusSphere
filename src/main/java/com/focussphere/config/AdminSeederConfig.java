package com.focussphere.config;

import com.focussphere.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminSeederConfig {

    @Bean
    public CommandLineRunner adminSeeder(
            UserService userService,
            @Value("${focussphere.admin.name}") String adminName,
            @Value("${focussphere.admin.email}") String adminEmail,
            @Value("${focussphere.admin.phone}") String adminPhone,
            @Value("${focussphere.admin.rollNo}") String adminRollNo,
            @Value("${focussphere.admin.password}") String adminPassword
    ) {
        return args -> userService.ensureAdmin(adminName, adminEmail, adminPhone, adminRollNo, adminPassword);
    }
}
