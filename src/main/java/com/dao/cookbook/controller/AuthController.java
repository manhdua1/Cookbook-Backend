package com.dao.cookbook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dao.cookbook.config.JwtUtil;
import com.dao.cookbook.dto.request.RegisterRequestDTO;
import com.dao.cookbook.service.OtpService;
import com.dao.cookbook.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, OtpService otpService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.otpService = otpService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        try {
            System.out.println("Login attempt - username/email: " + username);
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            System.out.println("Login successful for: " + username);
            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            System.err.println("Login failed for username: " + username);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thông tin đăng nhập không hợp lệ");
        }
    }

    /**
     * Bước 1: Gửi OTP quên mật khẩu
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody com.dao.cookbook.dto.request.ForgotPasswordRequestDTO dto) {
        try {
            // Kiểm tra email có tồn tại trong hệ thống không
            if (!userService.emailExists(dto.getEmail())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Email không tồn tại trong hệ thống");
            }

            // Gửi OTP qua email
            otpService.generateAndSendForgotPasswordOtp(dto.getEmail());
            
            return ResponseEntity.ok("OTP khôi phục mật khẩu đã được gửi đến email");
        } catch (Exception e) {
            System.err.println("Error sending forgot password OTP: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi gửi OTP: " + e.getMessage());
        }
    }

    /**
     * Bước 2: Xác thực OTP và đặt lại mật khẩu mới
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody com.dao.cookbook.dto.request.ResetPasswordRequestDTO dto) {
        try {
            // Xác thực OTP
            if (!otpService.verifyForgotPasswordOtp(dto.getEmail(), dto.getOtp())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("OTP không hợp lệ hoặc đã hết hạn");
            }

            // Reset mật khẩu
            userService.resetPassword(dto.getEmail(), dto.getNewPassword());
            
            return ResponseEntity.ok("Đặt lại mật khẩu thành công");
        } catch (RuntimeException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error resetting password: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đặt lại mật khẩu");
        }
    }

    /**
     * Đổi mật khẩu (yêu cầu đăng nhập)
     * POST /api/auth/change-password
     * Requires Authentication
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody com.dao.cookbook.dto.request.ChangePasswordRequestDTO dto) {
        try {
            // Lấy email từ JWT token trong SecurityContext
            Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Người dùng chưa đăng nhập");
            }

            String email = authentication.getName();

            // Đổi mật khẩu (sẽ kiểm tra mật khẩu cũ trong service)
            userService.changePassword(email, dto.getOldPassword(), dto.getNewPassword());
            
            return ResponseEntity.ok("Đổi mật khẩu thành công");
        } catch (RuntimeException e) {
            System.err.println("Error changing password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đổi mật khẩu");
        }
    }
}
