package com.dao.cookbook.controller;

import com.dao.cookbook.dto.request.RatingRequestDTO;
import com.dao.cookbook.dto.response.RatingResponseDTO;
import com.dao.cookbook.dto.response.UserResponseDTO;
import com.dao.cookbook.entity.RecipeRatingEntity;
import com.dao.cookbook.mapper.CommentRatingMapper;
import com.dao.cookbook.service.RecipeRatingService;
import com.dao.cookbook.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for Recipe Ratings.
 */
@RestController
@RequestMapping("/api/recipes/{recipeId}/ratings")
public class RatingController {
    
    private final RecipeRatingService ratingService;
    private final UserService userService;
    private final CommentRatingMapper mapper;
    
    public RatingController(RecipeRatingService ratingService, UserService userService, CommentRatingMapper mapper) {
        this.ratingService = ratingService;
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
     * Rate a recipe (add or update rating).
     * POST /api/recipes/{recipeId}/ratings
     */
    @PostMapping
    public ResponseEntity<?> rateRecipe(@PathVariable Long recipeId, @Valid @RequestBody RatingRequestDTO dto) {
        try {
            Long userId = getCurrentUserId();
            RecipeRatingEntity rating = ratingService.rateRecipe(userId, recipeId, dto.getRating());
            RatingResponseDTO response = mapper.ratingToResponse(rating);
            
            Map<String, Object> result = new HashMap<>();
            result.put("rating", response);
            result.put("averageRating", ratingService.getAverageRating(recipeId));
            result.put("ratingsCount", ratingService.getRatingsCount(recipeId));
            result.put("message", "Đánh giá thành công");
            
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Get current user's rating for a recipe.
     * GET /api/recipes/{recipeId}/ratings/my-rating
     */
    @GetMapping("/my-rating")
    public ResponseEntity<?> getMyRating(@PathVariable Long recipeId) {
        try {
            Long userId = getCurrentUserId();
            Optional<RecipeRatingEntity> rating = ratingService.getUserRating(userId, recipeId);
            
            if (rating.isPresent()) {
                RatingResponseDTO response = mapper.ratingToResponse(rating.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn chưa đánh giá công thức này");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    /**
     * Delete rating.
     * DELETE /api/recipes/{recipeId}/ratings
     */
    @DeleteMapping
    public ResponseEntity<String> deleteRating(@PathVariable Long recipeId) {
        try {
            Long userId = getCurrentUserId();
            ratingService.deleteRating(userId, recipeId);
            return ResponseEntity.ok("Xóa đánh giá thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Get rating statistics for a recipe.
     * GET /api/recipes/{recipeId}/ratings/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRatingStats(@PathVariable Long recipeId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("averageRating", ratingService.getAverageRating(recipeId));
            stats.put("ratingsCount", ratingService.getRatingsCount(recipeId));
            stats.put("ratingDistribution", ratingService.getRatingDistribution(recipeId));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    /**
     * Get all ratings for a recipe (with user info).
     * GET /api/recipes/{recipeId}/ratings
     */
    @GetMapping
    public ResponseEntity<List<RatingResponseDTO>> getAllRatings(@PathVariable Long recipeId) {
        try {
            List<RecipeRatingEntity> ratings = ratingService.getRecipeRatings(recipeId);
            List<RatingResponseDTO> response = ratings.stream()
                    .map(mapper::ratingToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
