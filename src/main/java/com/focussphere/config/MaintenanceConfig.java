package com.focussphere.config;

import com.focussphere.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MaintenanceConfig {

    @Bean
    @ConditionalOnProperty(
            prefix = "focussphere.maintenance",
            name = "purge-non-admin-users-on-startup",
            havingValue = "true")
    public CommandLineRunner purgeNonAdminUsersOnStartup(UserService userService) {
        return args -> userService.purgeNonAdminUsers();
    }
}