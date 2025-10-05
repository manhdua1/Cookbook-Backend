package com.dao.cookbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dao.cookbook.dto.request.UserRequestDTO;
import com.dao.cookbook.dto.response.UserResponseDTO;
import com.dao.cookbook.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "API quản lý người dùng")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Lấy danh sách tất cả người dùng", description = "Trả về danh sách tất cả người dùng")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Danh sách người dùng được trả về thành công")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Lấy thông tin người dùng theo ID", description = "Trả về thông tin chi tiết của người dùng dựa trên ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thông tin người dùng được trả về thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng với ID này")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID của người dùng cần lấy", example = "1") 
            @PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Tạo người dùng mới", description = "Tạo một người dùng mới với thông tin trong request body")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Người dùng được tạo thành công")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Parameter(description = "Thông tin người dùng mới") 
            @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @Operation(summary = "Cập nhật thông tin người dùng", description = "Cập nhật thông tin profile của người dùng, không thay đổi email/password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng để cập nhật")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID người dùng cần cập nhật", example = "1") 
            @PathVariable Long id,
            @Parameter(description = "Thông tin người dùng cần cập nhật") 
            @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @Operation(summary = "Xóa người dùng theo ID", description = "Xóa người dùng dựa trên ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng để xóa")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID người dùng cần xóa", example = "1") 
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Kiểm tra email đã tồn tại", description = "Kiểm tra xem email đã được đăng ký chưa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Trả về true nếu email tồn tại, false nếu không")
    })
    @GetMapping("/exists")
    public ResponseEntity<Boolean> emailExists(
            @Parameter(description = "Email cần kiểm tra", example = "test@example.com") 
            @RequestParam String email) {
        return ResponseEntity.ok(userService.emailExists(email));
    }
}
