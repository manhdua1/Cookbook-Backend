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

    // Tạo và gửi OTP cho quên mật khẩu
    public void generateAndSendForgotPasswordOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 số
        otpCache.put("forgot_" + email, otp); // Thêm prefix để phân biệt với OTP đăng ký

        // Gửi email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP khôi phục mật khẩu - Cookbook");
        message.setText("Mã OTP khôi phục mật khẩu của bạn là: " + otp + "\n\n" +
                "Mã này có hiệu lực trong 5 phút.\n" +
                "Nếu bạn không yêu cầu khôi phục mật khẩu, vui lòng bỏ qua email này.");
        mailSender.send(message);
    }

    // Kiểm tra OTP cho quên mật khẩu
    public boolean verifyForgotPasswordOtp(String email, String otp) {
        String cached = otpCache.get("forgot_" + email);
        if (cached != null && cached.equals(otp)) {
            otpCache.remove("forgot_" + email); // Xóa sau khi dùng
            return true;
        }
        return false;
    }
}
