package com.dao.cookbook.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dao.cookbook.dto.request.RegisterRequestDTO;
import com.dao.cookbook.dto.request.UserRequestDTO;
import com.dao.cookbook.dto.response.UserResponseDTO;
import com.dao.cookbook.entity.UserEntity;
import com.dao.cookbook.mapper.UserMapper;
import com.dao.cookbook.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // Lấy tất cả user
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    // Lấy user theo id
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse);
    }

    // Tạo user mới
    public UserResponseDTO createUser(UserRequestDTO dto) {
        UserEntity user = userMapper.toEntity(dto);
        UserEntity saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    // Cập nhật user (không update email/password tạm thời)
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateEntity(user, dto);
        UserEntity updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    // Xóa user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // Kiểm tra email tồn tại
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Lấy user theo email
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse);
    }

    // Đăng ký user mới
    public UserResponseDTO registerUser(RegisterRequestDTO dto) {
        // 1. Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // 2. Kiểm tra password khớp confirmPassword
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        // 3. Tạo entity và mã hóa mật khẩu
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Các field khác (nếu có mặc định)
        user.setFullName("Người dùng mới");
        user.setAvatarUrl(null);
        user.setBio(null);
        user.setHometown(null);

        // 4. Lưu user
        UserEntity saved = userRepository.save(user);

        // 5. Trả về response
        return userMapper.toResponse(saved);
    }
}
