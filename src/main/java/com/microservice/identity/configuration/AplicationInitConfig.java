package com.microservice.identity.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microservice.identity.entity.Role;
import com.microservice.identity.entity.User;
import com.microservice.identity.repository.RoleRepository;
import com.microservice.identity.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AplicationInitConfig {

    private static final String ADMIN_USER_NAME = "admin";

    private static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (!roleRepository.existsById("USER")) {
                roleRepository.save(Role.builder()
                        .name("USER")
                        .description("User role")
                        .build());
            }
            Role adminRole = roleRepository.findById("ADMIN").orElseGet(() -> roleRepository.save(Role.builder()
                    .name("ADMIN")
                    .description("Admin role")
                    .build()));
            if (!userRepository.existsByUsername(ADMIN_USER_NAME)) {
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                User adminUser = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .build();
                userRepository.save(adminUser);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}
