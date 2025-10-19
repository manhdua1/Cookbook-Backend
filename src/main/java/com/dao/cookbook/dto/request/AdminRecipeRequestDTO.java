package com.dao.cookbook.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for creating a recipe as admin with specific userId.
 * This allows admins to create recipes on behalf of users.
 */
@Data
public class AdminRecipeRequestDTO {
    
    @NotNull(message = "User ID không được để trống")
    @Positive(message = "User ID phải lớn hơn 0")
    private Long userId;
    
    @NotBlank(message = "Tiêu đề công thức không được để trống")
    private String title;
    
    private String imageUrl;
    
    @NotNull(message = "Số khẩu phần không được để trống")
    @Positive(message = "Số khẩu phần phải lớn hơn 0")
    private Integer servings;
    
    @Positive(message = "Thời gian nấu phải lớn hơn 0")
    private Integer cookingTime;
    
    @Valid
    private List<IngredientDTO> ingredients = new ArrayList<>();
    
    @Valid
    private List<RecipeStepDTO> steps = new ArrayList<>();
}
