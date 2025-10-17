package com.dao.cookbook.controller;

import com.dao.cookbook.dto.request.RecipeRequestDTO;
import com.dao.cookbook.dto.response.RecipeResponseDTO;
import com.dao.cookbook.dto.response.UserResponseDTO;
import com.dao.cookbook.service.RecipeService;
import com.dao.cookbook.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Recipe management.
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;

    public RecipeController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }

    /**
     * Get current authenticated user ID from security context.
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
     * Get all recipes.
     * GET /api/recipes
     */
    @GetMapping("/getRecipes")
    public ResponseEntity<List<RecipeResponseDTO>> getAllRecipes() {
        List<RecipeResponseDTO> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipe by ID.getRecipes
     * GET /api/recipes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponseDTO> getRecipeById(@PathVariable Long id) {
        try {
            RecipeResponseDTO recipe = recipeService.getRecipeById(id);
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
        List<RecipeResponseDTO> recipes = recipeService.getRecipesByUserId(userId);
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
            List<RecipeResponseDTO> recipes = recipeService.getRecipesByUserId(userId);
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
        List<RecipeResponseDTO> recipes = recipeService.searchRecipesByTitle(title);
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
}
