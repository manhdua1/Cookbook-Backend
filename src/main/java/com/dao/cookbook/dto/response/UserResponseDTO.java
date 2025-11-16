package com.dao.cookbook.dto.response;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String hometown;
    private String provider;
    private int followersCount;
    private int followingCount;
}