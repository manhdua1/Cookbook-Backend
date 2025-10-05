package com.dao.cookbook.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final Map<String, String> otpCache = new ConcurrentHashMap<>();
    private final JavaMailSender mailSender; // cần cấu hình SMTP

    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Tạo và gửi OTP
    public void generateAndSendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 số
        otpCache.put(email, otp);

        // Gửi email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP đăng ký tài khoản");
        message.setText("Mã OTP của bạn là: " + otp);
        mailSender.send(message);
    }

    // Kiểm tra OTP
    public boolean verifyOtp(String email, String otp) {
        String cached = otpCache.get(email);
        if (cached != null && cached.equals(otp)) {
            otpCache.remove(email); // Xóa sau khi dùng
            return true;
        }
        return false;
    }
}
