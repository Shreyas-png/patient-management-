package com.auth.service.repository;

import com.auth.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Boolean existsUserByEmail(String email);
    List<User> findUsersByRole(String role);
}
