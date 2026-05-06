package com.mariluz.auth_service;

import com.mariluz.auth_service.model.Role;
import com.mariluz.auth_service.model.User;
import com.mariluz.auth_service.repository.AuthRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminLoader {

    @Bean
    CommandLineRunner init(AuthRepo repo) {
        return args -> {
            String email = "mariluz@costurera.cl";

            if (!repo.existsByEmail(email)) {
                repo.save(
                    User.builder()
                        .name("MariLuz")
                        .email(email)
                        .password("123456")
                        .role(Role.ADMIN)
                        .build()
                );
            }
        };
    }
}
