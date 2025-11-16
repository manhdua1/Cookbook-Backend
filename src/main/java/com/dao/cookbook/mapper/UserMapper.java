package com.dao.cookbook.mapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.dao.cookbook.dto.request.UserRequestDTO;
import com.dao.cookbook.dto.response.UserResponseDTO;
import com.dao.cookbook.entity.UserEntity;

@Component
public class UserMapper {

    private final BCryptPasswordEncoder passwordEncoder;

    public UserMapper() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserResponseDTO toResponse(UserEntity user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setHometown(user.getHometown());
        dto.setProvider(user.getProvider());
        dto.setFollowersCount(user.getFollowersCount());
        dto.setFollowingCount(user.getFollowingCount());
        return dto;
    }

    public UserEntity toEntity(UserRequestDTO dto) {
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setBio(dto.getBio());
        user.setHometown(dto.getHometown());
        return user;
    }

    public void updateEntity(UserEntity user, UserRequestDTO dto) {
        user.setFullName(dto.getFullName());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setBio(dto.getBio());
        user.setHometown(dto.getHometown());
        // Không update email và password tạm thời
    }
}
