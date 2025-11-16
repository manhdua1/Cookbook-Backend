package com.dao.cookbook.controller;

import com.dao.cookbook.dto.request.CommentRequestDTO;
import com.dao.cookbook.dto.response.CommentResponseDTO;
import com.dao.cookbook.dto.response.UserResponseDTO;
import com.dao.cookbook.entity.RecipeCommentEntity;
import com.dao.cookbook.mapper.CommentRatingMapper;
import com.dao.cookbook.service.RecipeCommentService;
import com.dao.cookbook.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Recipe Comments.
 */
@RestController
@RequestMapping("/api/recipes/{recipeId}/comments")
public class CommentController {
    
    private final RecipeCommentService commentService;
    private final UserService userService;
    private final CommentRatingMapper mapper;
    
    public CommentController(RecipeCommentService commentService, UserService userService, CommentRatingMapper mapper) {
        this.commentService = commentService;
        this.userService = userService;
        this.mapper = mapper;
    }
    
    /**
     * Get current authenticated user ID.
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
     * Get all comments for a recipe.
     * GET /api/recipes/{recipeId}/comments
     */
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long recipeId) {
        try {
            List<RecipeCommentEntity> comments = commentService.getRecipeComments(recipeId);
            List<CommentResponseDTO> response = comments.stream()
                    .map(comment -> {
                        CommentResponseDTO dto = mapper.commentToResponse(comment);
                        // Get replies for each comment
                        List<RecipeCommentEntity> replies = commentService.getCommentReplies(comment.getId());
                        dto.setReplies(replies.stream()
                                .map(mapper::commentToResponse)
                                .collect(Collectors.toList()));
                        return dto;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    /**
     * Add a comment to a recipe.
     * POST /api/recipes/{recipeId}/comments
     */
    @PostMapping
    public ResponseEntity<?> addComment(@PathVariable Long recipeId, @Valid @RequestBody CommentRequestDTO dto) {
        try {
            Long userId = getCurrentUserId();
            RecipeCommentEntity comment = commentService.addComment(
                    userId, 
                    recipeId, 
                    dto.getComment(), 
                    dto.getParentCommentId()
            );
            CommentResponseDTO response = mapper.commentToResponse(comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Update a comment.
     * PUT /api/recipes/{recipeId}/comments/{commentId}
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long recipeId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDTO dto) {
        try {
            Long userId = getCurrentUserId();
            RecipeCommentEntity comment = commentService.updateComment(commentId, userId, dto.getComment());
            CommentResponseDTO response = mapper.commentToResponse(comment);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Delete a comment.
     * DELETE /api/recipes/{recipeId}/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long recipeId, @PathVariable Long commentId) {
        try {
            Long userId = getCurrentUserId();
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok("Xóa bình luận thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
