package com.dao.cookbook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dao.cookbook.dto.request.RegisterRequestDTO;
import com.dao.cookbook.service.OtpService;
import com.dao.cookbook.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, OtpService otpService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    // Bước 1: Gửi OTP
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        otpService.generateAndSendOtp(email);
        return ResponseEntity.ok("OTP đã được gửi đến email");
    }

    // Bước 2: Xác thực OTP và tạo tài khoản
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO dto) {
        if (!otpService.verifyOtp(dto.getEmail(), dto.getOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ");
        }

        userService.registerUser(dto); // để UserService tự encode
        return ResponseEntity.ok("Đăng ký thành công");
}

}
