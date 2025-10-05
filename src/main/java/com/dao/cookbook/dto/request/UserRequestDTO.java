package com.dao.cookbook.dto.request;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String email;
    private String password;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String hometown;
}