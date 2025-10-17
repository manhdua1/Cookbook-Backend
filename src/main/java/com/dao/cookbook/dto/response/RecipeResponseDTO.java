package com.dao.cookbook.dto.response;

import lombok.Data;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.dao.cookbook.dto.request.IngredientDTO;
import com.dao.cookbook.dto.request.RecipeStepDTO;

/**
 * DTO for recipe response.
 */
@Data
public class RecipeResponseDTO {
    
    private Long id;
    
    private String title;
    
    private String imageUrl;
    
    private Integer servings;
    
    private Integer cookingTime;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private List<IngredientDTO> ingredients = new ArrayList<>();
    
    private List<RecipeStepDTO> steps = new ArrayList<>();
    
    private Integer likesCount;
    
    private Boolean isLikedByCurrentUser;
    
    private Integer bookmarksCount;
    
    private Boolean isBookmarkedByCurrentUser;
    
    private Timestamp createdAt;
    
    private Timestamp updatedAt;
}
