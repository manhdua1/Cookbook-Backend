package com.dao.cookbook.controller;

import com.dao.cookbook.dto.request.AdminRecipeRequestDTO;
import com.dao.cookbook.dto.response.RecipeResponseDTO;
import com.dao.cookbook.service.RecipeService;
import com.dao.cookbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin API", description = "API dành cho việc quản lý công thức nấu ăn (mọi user đã đăng nhập có thể sử dụng)")
public class AdminController {

    private final RecipeService recipeService;
    private final UserService userService;

    public AdminController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }

    /**
     * Get current authenticated admin user ID
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
        summary = "Thêm công thức nấu ăn", 
        description = "Thêm công thức nấu ăn mới vào hệ thống. Công thức sẽ được gán cho user hiện tại."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Thêm công thức thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @PostMapping("/recipes")
    public ResponseEntity<?> createRecipe(
            @Parameter(description = "Thông tin công thức nấu ăn")
            @Valid @RequestBody AdminRecipeRequestDTO dto) {
        try {
            // Nếu có userId trong DTO thì dùng, không thì lấy từ JWT
            Long userId = dto.getUserId() != null ? dto.getUserId() : getCurrentUserId();
            RecipeResponseDTO recipe = recipeService.createRecipe(dto, userId);
            
            // Reload with complete info
            recipe = recipeService.getRecipeById(recipe.getId(), getCurrentUserId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
        } catch (RuntimeException e) {
            System.err.println("Error creating recipe: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi tạo công thức: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Thêm nhiều công thức nấu ăn cùng lúc", 
        description = "Thêm nhiều công thức nấu ăn mới vào hệ thống cùng một lúc. Tất cả công thức sẽ được gán cho user hiện tại."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Thêm công thức thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @PostMapping("/recipes/bulk")
    public ResponseEntity<?> createRecipesBulk(
            @Parameter(description = "Danh sách công thức nấu ăn")
            @Valid @RequestBody java.util.List<AdminRecipeRequestDTO> dtos) {
        try {
            Long currentUserId = getCurrentUserId();
            
            java.util.List<RecipeResponseDTO> createdRecipes = new java.util.ArrayList<>();
            java.util.List<String> errors = new java.util.ArrayList<>();
            
            for (int i = 0; i < dtos.size(); i++) {
                try {
                    AdminRecipeRequestDTO dto = dtos.get(i);
                    // Nếu có userId trong DTO thì dùng, không thì lấy từ JWT
                    Long userId = dto.getUserId() != null ? dto.getUserId() : currentUserId;
                    RecipeResponseDTO recipe = recipeService.createRecipe(dto, userId);
                    recipe = recipeService.getRecipeById(recipe.getId(), currentUserId);
                    createdRecipes.add(recipe);
                } catch (Exception e) {
                    errors.add("Recipe " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "Đã thêm " + createdRecipes.size() + " công thức");
            response.put("totalRequested", dtos.size());
            response.put("totalCreated", createdRecipes.size());
            response.put("recipes", createdRecipes);
            
            if (!errors.isEmpty()) {
                response.put("errors", errors);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            System.err.println("Error creating recipes bulk: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi tạo công thức: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Cập nhật công thức nấu ăn", 
        description = "Cập nhật bất kỳ công thức nào trong hệ thống (chỉ owner hoặc admin mới được cập nhật)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy công thức"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @PutMapping("/recipes/{id}")
    public ResponseEntity<?> updateRecipe(
            @Parameter(description = "ID công thức cần cập nhật", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Thông tin cập nhật")
            @Valid @RequestBody AdminRecipeRequestDTO dto) {
        try {
            Long userId = getCurrentUserId();
            RecipeResponseDTO recipe = recipeService.updateRecipeByAdmin(id, dto);
            
            // Reload with complete info
            recipe = recipeService.getRecipeById(recipe.getId(), userId);
            
            return ResponseEntity.ok(recipe);
        } catch (RuntimeException e) {
            System.err.println("Error updating recipe: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi cập nhật công thức: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Xóa công thức nấu ăn", 
        description = "Xóa bất kỳ công thức nào trong hệ thống (chỉ owner hoặc admin mới được xóa)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy công thức"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<String> deleteRecipe(
            @Parameter(description = "ID công thức cần xóa", example = "1")
            @PathVariable Long id) {
        try {
            recipeService.deleteRecipeByAdmin(id);
            return ResponseEntity.ok("Xóa công thức thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
