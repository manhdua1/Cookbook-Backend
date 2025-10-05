package com.dao.cookbook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dao.cookbook.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    //Check if a user exists by email
    boolean existsByEmail(String email);

    //Find a user by email
    Optional<UserEntity> findByEmail(String email);

    //Find a user by id
    Optional<UserEntity> findById(Long id);

    //Find a user by email and provider (for OAuth and OTP users)
    Optional<UserEntity> findByEmailAndProvider(String email, String provider);
}
