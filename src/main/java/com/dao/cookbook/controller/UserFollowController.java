package com.dao.cookbook.controller;

import com.dao.cookbook.dto.response.UserResponseDTO;
import com.dao.cookbook.entity.UserEntity;
import com.dao.cookbook.mapper.UserMapper;
import com.dao.cookbook.service.UserFollowService;
import com.dao.cookbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserFollowController {

    @Autowired
    private UserFollowService userFollowService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    /**
     * Get current authenticated user ID from security context.
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }
        
        String email = authentication.getName();
        UserResponseDTO user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        return user.getId();
    }

    /**
     * Follow a user
     * POST /api/users/{userId}/follow
     */
    @PostMapping("/{userId}/follow")
    public ResponseEntity<?> followUser(@PathVariable Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            userFollowService.followUser(currentUserId, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully followed user");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Unfollow a user
     * DELETE /api/users/{userId}/follow
     */
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<?> unfollowUser(@PathVariable Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            userFollowService.unfollowUser(currentUserId, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully unfollowed user");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get list of users that the specified user is following
     * GET /api/users/{userId}/following
     */
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserResponseDTO>> getFollowing(@PathVariable Long userId) {
        List<UserEntity> following = userFollowService.getFollowing(userId);
        List<UserResponseDTO> response = following.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Get list of followers of the specified user
     * GET /api/users/{userId}/followers
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserResponseDTO>> getFollowers(@PathVariable Long userId) {
        List<UserEntity> followers = userFollowService.getFollowers(userId);
        List<UserResponseDTO> response = followers.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Check if current user is following the specified user
     * GET /api/users/{userId}/is-following
     */
    @GetMapping("/{userId}/is-following")
    public ResponseEntity<Map<String, Boolean>> isFollowing(@PathVariable Long userId) {
        Long currentUserId = getCurrentUserId();
        boolean isFollowing = userFollowService.isFollowing(currentUserId, userId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFollowing", isFollowing);
        return ResponseEntity.ok(response);
    }

    /**
     * Get follower and following counts for a user
     * GET /api/users/{userId}/follow-stats
     */
    @GetMapping("/{userId}/follow-stats")
    public ResponseEntity<Map<String, Long>> getFollowStats(@PathVariable Long userId) {
        long followerCount = userFollowService.getFollowerCount(userId);
        long followingCount = userFollowService.getFollowingCount(userId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("followersCount", followerCount);
        response.put("followingCount", followingCount);
        return ResponseEntity.ok(response);
    }
}
