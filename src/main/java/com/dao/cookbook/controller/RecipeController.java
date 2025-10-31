package com.dao.cookbook.controller;

import com.dao.cookbook.dto.request.AdminRecipeRequestDTO;
import com.dao.cookbook.dto.request.RecipeRequestDTO;
import com.dao.cookbook.dto.response.RecipeResponseDTO;
import com.dao.cookbook.dto.response.UserResponseDTO;
import com.dao.cookbook.service.RecipeBookmarkService;
import com.dao.cookbook.service.RecipeLikeService;
import com.dao.cookbook.service.RecipeService;
import com.dao.cookbook.service.SearchHistoryService;
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

/**
 * REST Controller for Recipe management.
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final RecipeLikeService recipeLikeService;
    private final RecipeBookmarkService recipeBookmarkService;
    private final SearchHistoryService searchHistoryService;
    private final com.dao.cookbook.service.RecipeViewHistoryService viewHistoryService;

    public RecipeController(RecipeService recipeService, UserService userService, RecipeLikeService recipeLikeService, RecipeBookmarkService recipeBookmarkService, SearchHistoryService searchHistoryService, com.dao.cookbook.service.RecipeViewHistoryService viewHistoryService) {
        this.recipeService = recipeService;
        this.userService = userService;
        this.recipeLikeService = recipeLikeService;
        this.recipeBookmarkService = recipeBookmarkService;
        this.searchHistoryService = searchHistoryService;
        this.viewHistoryService = viewHistoryService;
    }

    /**
     * Get feed of recipes from users the current user is following
     * GET /api/recipes/following-feed
     */
    @GetMapping("/following-feed")
    public ResponseEntity<List<RecipeResponseDTO>> getFollowingFeed() {
        Long currentUserId = getCurrentUserId();
        List<RecipeResponseDTO> feed = recipeService.getFollowingFeed(currentUserId);
        return ResponseEntity.ok(feed);
    }

    /**
     * Get recently viewed recipes by current user
     * GET /api/recipes/recently-viewed
     */
    @GetMapping("/recently-viewed")
    public ResponseEntity<List<RecipeResponseDTO>> getRecentlyViewedRecipes(
            @RequestParam(required = false) Integer limit) {
        Long currentUserId = getCurrentUserId();
        List<RecipeResponseDTO> recipes = viewHistoryService.getRecentlyViewedRecipes(currentUserId, limit);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Clear view history for current user
     * DELETE /api/recipes/recently-viewed
     */
    @DeleteMapping("/recently-viewed")
    public ResponseEntity<Map<String, String>> clearViewHistory() {
        Long currentUserId = getCurrentUserId();
        viewHistoryService.clearUserViewHistory(currentUserId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "View history cleared successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Remove a specific recipe from view history
     * DELETE /api/recipes/recently-viewed/{recipeId}
     */
    @DeleteMapping("/recently-viewed/{recipeId}")
    public ResponseEntity<Map<String, String>> removeFromViewHistory(
            @PathVariable Long recipeId) {
        Long currentUserId = getCurrentUserId();
        viewHistoryService.removeRecipeFromHistory(currentUserId, recipeId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Recipe removed from view history successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user ID from security context.
     * Throws RuntimeException if user is not authenticated.
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }
        
        // Get email from authentication (JWT token contains email as subject)
        String email = authentication.getName();
        
        // Find user by email and get their ID
        UserResponseDTO user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        return user.getId();
    }
    
    /**
     * Get current authenticated user ID or null if not authenticated.
     * Used for public endpoints that can show personalized content for logged-in users.
     */
    private Long getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Get all recipes.
     * GET /api/recipes
     */
    @GetMapping("/getRecipes")
    public ResponseEntity<List<RecipeResponseDTO>> getAllRecipes() {
        Long currentUserId = getCurrentUserIdOrNull();
        List<RecipeResponseDTO> recipes = recipeService.getAllRecipes(currentUserId);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipe by ID.getRecipes
     * GET /api/recipes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponseDTO> getRecipeById(@PathVariable Long id) {
        try {
            Long currentUserId = getCurrentUserIdOrNull();
            RecipeResponseDTO recipe = recipeService.getRecipeById(id, currentUserId);
            
            // Save view history if user is authenticated
            if (currentUserId != null) {
                try {
                    viewHistoryService.saveViewHistory(currentUserId, id);
                } catch (Exception e) {
                    // Don't fail the request if view history save fails
                    System.err.println("Failed to save view history: " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok(recipe);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Get recipes by user ID.
     * GET /api/recipes/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeResponseDTO>> getRecipesByUserId(@PathVariable Long userId) {
        Long currentUserId = getCurrentUserIdOrNull();
        List<RecipeResponseDTO> recipes = recipeService.getRecipesByUserId(userId, currentUserId);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipes of current authenticated user.
     * GET /api/recipes/my-recipes
     */
    @GetMapping("/my-recipes")
    public ResponseEntity<List<RecipeResponseDTO>> getMyRecipes() {
        try {
            Long userId = getCurrentUserId();
            List<RecipeResponseDTO> recipes = recipeService.getRecipesByUserId(userId, userId);
            return ResponseEntity.ok(recipes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Search recipes by title.
     * GET /api/recipes/search?title={title}
     */
    @GetMapping("/search")
    public ResponseEntity<List<RecipeResponseDTO>> searchRecipes(@RequestParam String title) {
        Long currentUserId = getCurrentUserIdOrNull();
        
        // Tự động lưu lịch sử tìm kiếm nếu user đã đăng nhập
        if (currentUserId != null && title != null && !title.trim().isEmpty()) {
            try {
                searchHistoryService.saveSearchHistory(currentUserId, title);
            } catch (Exception e) {
                // Không throw exception nếu lưu lịch sử thất bại
                System.err.println("Lỗi lưu lịch sử tìm kiếm: " + e.getMessage());
            }
        }
        
        List<RecipeResponseDTO> recipes = recipeService.searchRecipesByTitle(title, currentUserId);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Create a new recipe.
     * POST /api/recipes
     */
    @PostMapping
    public ResponseEntity<?> createRecipe(@Valid @RequestBody RecipeRequestDTO dto) {
        try {
            Long userId = getCurrentUserId();
            RecipeResponseDTO recipe = recipeService.createRecipe(dto, userId);
            // Reload with like info for the creator
            recipe = recipeService.getRecipeById(recipe.getId(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
        } catch (RuntimeException e) {
            // Log the error for debugging
            System.err.println("Error creating recipe: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi tạo công thức: " + e.getMessage());
        }
    }

    /**
     * Update an existing recipe.
     * PUT /api/recipes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeRequestDTO dto) {
        try {
            Long userId = getCurrentUserId();
            RecipeResponseDTO recipe = recipeService.updateRecipe(id, dto, userId);
            // Reload with like info for the updater
            recipe = recipeService.getRecipeById(recipe.getId(), userId);
            return ResponseEntity.ok(recipe);
        } catch (RuntimeException e) {
            System.err.println("Error updating recipe: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi cập nhật công thức: " + e.getMessage());
        }
    }

    /**
     * Delete a recipe.
     * DELETE /api/recipes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            recipeService.deleteRecipe(id, userId);
            return ResponseEntity.ok("Xóa công thức thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Like a recipe.
     * POST /api/recipes/{id}/like
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likeRecipe(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            boolean success = recipeLikeService.likeRecipe(userId, id);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("message", "Đã thích công thức");
                response.put("liked", true);
                response.put("likesCount", recipeLikeService.getLikesCount(id));
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Bạn đã thích công thức này rồi");
                response.put("liked", true);
                response.put("likesCount", recipeLikeService.getLikesCount(id));
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lỗi: " + e.getMessage());
            response.put("liked", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Unlike a recipe.
     * DELETE /api/recipes/{id}/like
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> unlikeRecipe(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            boolean success = recipeLikeService.unlikeRecipe(userId, id);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("message", "Đã bỏ thích công thức");
                response.put("liked", false);
                response.put("likesCount", recipeLikeService.getLikesCount(id));
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Bạn chưa thích công thức này");
                response.put("liked", false);
                response.put("likesCount", recipeLikeService.getLikesCount(id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lỗi: " + e.getMessage());
            response.put("liked", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Toggle like/unlike a recipe.
     * POST /api/recipes/{id}/toggle-like
     */
    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            boolean isLiked = recipeLikeService.toggleLike(userId, id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", isLiked ? "Đã thích công thức" : "Đã bỏ thích công thức");
            response.put("liked", isLiked);
            response.put("likesCount", recipeLikeService.getLikesCount(id));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Check if current user has liked a recipe.
     * GET /api/recipes/{id}/is-liked
     */
    @GetMapping("/{id}/is-liked")
    public ResponseEntity<Map<String, Object>> isLiked(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            boolean isLiked = recipeLikeService.isLikedByUser(userId, id);
            long likesCount = recipeLikeService.getLikesCount(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("liked", isLiked);
            response.put("likesCount", likesCount);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // If user is not authenticated, return false
            Map<String, Object> response = new HashMap<>();
            response.put("liked", false);
            response.put("likesCount", recipeLikeService.getLikesCount(id));
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Get all recipes liked by current user.
     * GET /api/recipes/liked
     */
    @GetMapping("/liked")
    public ResponseEntity<List<Long>> getLikedRecipes() {
        try {
            Long userId = getCurrentUserId();
            List<Long> likedRecipeIds = recipeLikeService.getLikedRecipeIds(userId);
            return ResponseEntity.ok(likedRecipeIds);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Bookmark a recipe.
     * POST /api/recipes/{id}/bookmark
     */
    @PostMapping("/{id}/bookmark")
    public ResponseEntity<Map<String, Object>> bookmarkRecipe(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            boolean success = recipeBookmarkService.bookmarkRecipe(userId, id);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("message", "Đã lưu công thức");
                response.put("bookmarked", true);
                response.put("bookmarksCount", recipeBookmarkService.getBookmarksCount(id));
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Bạn đã lưu công thức này rồi");
                response.put("bookmarked", true);
                response.put("bookmarksCount", recipeBookmarkService.getBookmarksCount(id));
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lỗi: " + e.getMessage());
            response.put("bookmarked", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Remove bookmark from a recipe.
     * DELETE /api/recipes/{id}/bookmark
     */
    @DeleteMapping("/{id}/bookmark")
    public ResponseEntity<Map<String, Object>> unbookmarkRecipe(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            boolean success = recipeBookmarkService.unbookmarkRecipe(userId, id);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("message", "Đã bỏ lưu công thức");
                response.put("bookmarked", false);
                response.put("bookmarksCount", recipeBookmarkService.getBookmarksCount(id));
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Bạn chưa lưu công thức này");
                response.put("bookmarked", false);
                response.put("bookmarksCount", recipeBookmarkService.getBookmarksCount(id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Toggle bookmark status.
     * POST /api/recipes/{id}/toggle-bookmark
     */
    @PostMapping("/{id}/toggle-bookmark")
    public ResponseEntity<Map<String, Object>> toggleBookmark(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            boolean isBookmarked = recipeBookmarkService.toggleBookmark(userId, id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", isBookmarked ? "Đã lưu công thức" : "Đã bỏ lưu công thức");
            response.put("bookmarked", isBookmarked);
            response.put("bookmarksCount", recipeBookmarkService.getBookmarksCount(id));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Check if current user has bookmarked a recipe.
     * GET /api/recipes/{id}/is-bookmarked
     */
    @GetMapping("/{id}/is-bookmarked")
    public ResponseEntity<Map<String, Object>> isBookmarked(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            boolean isBookmarked = recipeBookmarkService.isBookmarkedByUser(userId, id);
            long bookmarksCount = recipeBookmarkService.getBookmarksCount(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("bookmarked", isBookmarked);
            response.put("bookmarksCount", bookmarksCount);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // If user is not authenticated, return false
            Map<String, Object> response = new HashMap<>();
            response.put("bookmarked", false);
            response.put("bookmarksCount", recipeBookmarkService.getBookmarksCount(id));
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Get all recipes bookmarked by current user.
     * GET /api/recipes/bookmarked
     */
    @GetMapping("/bookmarked")
    public ResponseEntity<List<Long>> getBookmarkedRecipes() {
        try {
            Long userId = getCurrentUserId();
            List<Long> bookmarkedRecipeIds = recipeBookmarkService.getBookmarkedRecipeIds(userId);
            return ResponseEntity.ok(bookmarkedRecipeIds);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Create a new recipe as admin with specific userId.
     * POST /api/recipes/admin/create
     * This endpoint allows creating recipes on behalf of any user.
     */
    @PostMapping("/admin/create")
    public ResponseEntity<?> createRecipeAsAdmin(@Valid @RequestBody AdminRecipeRequestDTO dto) {
        try {
            // Verify the user exists
            userService.getUserById(dto.getUserId());
            
            // Create recipe for the specified user
            RecipeResponseDTO recipe = recipeService.createRecipe(dto, dto.getUserId());
            
            // Reload with complete info
            recipe = recipeService.getRecipeById(recipe.getId(), dto.getUserId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
        } catch (RuntimeException e) {
            System.err.println("Error creating recipe as admin: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi tạo công thức: " + e.getMessage());
        }
    }
}
