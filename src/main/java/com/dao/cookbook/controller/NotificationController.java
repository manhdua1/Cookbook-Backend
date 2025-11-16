package com.dao.cookbook.controller;

import com.dao.cookbook.dto.response.NotificationResponseDTO;
import com.dao.cookbook.entity.NotificationEntity;
import com.dao.cookbook.mapper.NotificationMapper;
import com.dao.cookbook.service.NotificationService;
import com.dao.cookbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification API", description = "API quản lý thông báo cho người dùng")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final UserService userService;

    public NotificationController(NotificationService notificationService,
                                 NotificationMapper notificationMapper,
                                 UserService userService) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
        this.userService = userService;
    }

    /**
     * Get current authenticated user ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }
        
        String email = authentication.getName();
        return userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"))
                .getId();
    }

    @Operation(
        summary = "Lấy tất cả thông báo", 
        description = "Lấy danh sách tất cả thông báo của người dùng hiện tại, sắp xếp theo thời gian mới nhất."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy thông báo thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping
    public ResponseEntity<?> getAllNotifications() {
        try {
            Long userId = getCurrentUserId();
            List<NotificationEntity> notifications = notificationService.getUserNotifications(userId);
            
            List<NotificationResponseDTO> response = notifications.stream()
                    .map(notificationMapper::toResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Lấy thông báo chưa đọc", 
        description = "Lấy danh sách các thông báo chưa đọc của người dùng hiện tại."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy thông báo thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications() {
        try {
            Long userId = getCurrentUserId();
            List<NotificationEntity> notifications = notificationService.getUnreadNotifications(userId);
            
            List<NotificationResponseDTO> response = notifications.stream()
                    .map(notificationMapper::toResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Đếm số thông báo chưa đọc", 
        description = "Lấy số lượng thông báo chưa đọc của người dùng hiện tại."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Đếm thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount() {
        try {
            Long userId = getCurrentUserId();
            long count = notificationService.countUnreadNotifications(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Đánh dấu thông báo đã đọc", 
        description = "Đánh dấu một thông báo cụ thể là đã đọc."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Đánh dấu thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy thông báo"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(
            @Parameter(description = "ID của thông báo", example = "1")
            @PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            boolean success = notificationService.markAsRead(id, userId);
            
            if (success) {
                return ResponseEntity.ok("Đã đánh dấu thông báo là đã đọc");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy thông báo");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Đánh dấu tất cả đã đọc", 
        description = "Đánh dấu tất cả thông báo chưa đọc là đã đọc."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Đánh dấu thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        try {
            Long userId = getCurrentUserId();
            int count = notificationService.markAllAsRead(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đã đánh dấu tất cả thông báo là đã đọc");
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Xóa thông báo", 
        description = "Xóa một thông báo cụ thể."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(
            @Parameter(description = "ID của thông báo cần xóa", example = "1")
            @PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            notificationService.deleteNotification(id, userId);
            
            return ResponseEntity.ok("Đã xóa thông báo");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Xóa tất cả thông báo", 
        description = "Xóa tất cả thông báo của người dùng hiện tại."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @DeleteMapping
    public ResponseEntity<String> deleteAllNotifications() {
        try {
            Long userId = getCurrentUserId();
            notificationService.deleteAllNotifications(userId);
            
            return ResponseEntity.ok("Đã xóa tất cả thông báo");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
