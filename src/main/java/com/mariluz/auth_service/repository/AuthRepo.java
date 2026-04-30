package com.mariluz.auth_service.repository;

import com.mariluz.auth_service.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepo extends JpaRepository<User, UUID> {
    public Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
