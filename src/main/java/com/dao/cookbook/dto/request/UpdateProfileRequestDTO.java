package com.dao.cookbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateProfileRequestDTO {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    private String fullName;

    @Size(max = 255, message = "Avatar URL không được vượt quá 255 ký tự")
    private String avatarUrl;

    @Size(max = 500, message = "Bio không được vượt quá 500 ký tự")
    private String bio;

    @Size(max = 100, message = "Quê quán không được vượt quá 100 ký tự")
    private String hometown;
}
