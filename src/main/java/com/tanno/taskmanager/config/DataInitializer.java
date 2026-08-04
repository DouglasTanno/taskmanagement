package com.tanno.taskmanager.config;

import com.tanno.taskmanager.enums.Role;
import com.tanno.taskmanager.model.User;
import com.tanno.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByEmail("admin@taskmanager.com").isEmpty()) {

                User admin = new User();

                admin.setName("Administrator");
                admin.setEmail("admin@taskmanager.com");
                admin.setPassword(
                        passwordEncoder.encode("admin123")
                );
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }
        };
    }
}