package com.dao.cookbook.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for creating a recipe as admin with specific userId.
 * This allows admins to create recipes on behalf of users.
 */
@Data
public class AdminRecipeRequestDTO {
    
    // userId không bắt buộc - sẽ lấy từ JWT token nếu không có
    private Long userId;
    
    @NotBlank(message = "Tiêu đề công thức không được để trống")
    private String title;
    
    private String imageUrl;
    
    @NotNull(message = "Số khẩu phần không được để trống")
    private Integer servings;
    
    private Integer cookingTime;
    
    @Valid
    private List<IngredientDTO> ingredients = new ArrayList<>();
    
    @Valid
    private List<RecipeStepDTO> steps = new ArrayList<>();
}
