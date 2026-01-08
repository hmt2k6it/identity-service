package com.microservice.identity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.identity.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByUsernameAndDeletedFalse(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByUserIdAndDeletedFalse(String userId);

    Optional<User> findByUsernameAndDeletedFalse(String username);

    List<User> findByDeletedFalse();
}
