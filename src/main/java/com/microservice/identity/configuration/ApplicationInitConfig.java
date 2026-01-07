package com.microservice.identity.configuration;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microservice.identity.entity.Permission;
import com.microservice.identity.entity.Role;
import com.microservice.identity.entity.User;
import com.microservice.identity.repository.PermissionRepository;
import com.microservice.identity.repository.RoleRepository;
import com.microservice.identity.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ApplicationInitConfig {

    private static final String ADMIN_USER_NAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            initPermission(permissionRepository, "USER:READ", "");
            initPermission(permissionRepository, "USER:UPDATE", "");
            initPermission(permissionRepository, "USER:DELETE", "");
            initPermission(permissionRepository, "USER:LIST", "");
            initPermission(permissionRepository, "USER:VERIFY", "");
            initPermission(permissionRepository, "USER:BAN", "");
            initPermission(permissionRepository, "ROLE:CREATE", "");
            initPermission(permissionRepository, "ROLE:UPDATE", "");
            initPermission(permissionRepository, "SYSTEM:LOGS", "");

            Set<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
            initRole(roleRepository, "ADMIN", "Super Administrator", allPermissions);

            Set<Permission> modPermissions = allPermissions.stream()
                    .filter(p -> p.getName().startsWith("USER"))
                    .collect(Collectors.toSet());
            initRole(roleRepository, "MODERATOR", "Content Moderator", modPermissions);

            initRole(roleRepository, "USER", "Standard User", null);

            if (!userRepository.existsByUsername(ADMIN_USER_NAME)) {
                Role adminRole = roleRepository.findById("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);

                User adminUser = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .email("thaydoi070@gmail.com")
                        .build();

                userRepository.save(adminUser);
                log.warn("ADMIN user has been created with default password: admin. Please change it immediately!");
            }
        };
    }

    private void initPermission(PermissionRepository repository, String name, String description) {
        if (!repository.existsById(name)) {
            repository.save(Permission.builder()
                    .name(name)
                    .description(description)
                    .build());
        }
    }

    private void initRole(RoleRepository repository, String name, String description, Set<Permission> permissions) {
        if (!repository.existsById(name)) {
            repository.save(Role.builder()
                    .name(name)
                    .description(description)
                    .permissions(permissions)
                    .build());
        }
    }
}