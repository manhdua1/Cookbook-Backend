package com.dao.cookbook.mapper;

import com.dao.cookbook.dto.request.IngredientDTO;
import com.dao.cookbook.dto.request.RecipeRequestDTO;
import com.dao.cookbook.dto.request.RecipeStepDTO;
import com.dao.cookbook.dto.request.StepImageDTO;
import com.dao.cookbook.dto.response.RecipeResponseDTO;
import com.dao.cookbook.entity.IngredientEntity;
import com.dao.cookbook.entity.RecipeEntity;
import com.dao.cookbook.entity.RecipeStepEntity;
import com.dao.cookbook.entity.StepImageEntity;
import com.dao.cookbook.service.RecipeBookmarkService;
import com.dao.cookbook.service.RecipeLikeService;
import com.dao.cookbook.service.RecipeRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for converting between Recipe entities and DTOs.
 */
@Component
public class RecipeMapper {
    
    @Autowired
    private RecipeLikeService recipeLikeService;
    
    @Autowired
    private RecipeBookmarkService recipeBookmarkService;
    
    @Autowired
    private RecipeRatingService recipeRatingService;
    
    /**
     * Convert RecipeRequestDTO to RecipeEntity.
     */
    public RecipeEntity toEntity(RecipeRequestDTO dto, Long userId) {
        RecipeEntity entity = new RecipeEntity();
        entity.setTitle(dto.getTitle());
        entity.setImageUrl(dto.getImageUrl());
        entity.setServings(dto.getServings());
        entity.setCookingTime(dto.getCookingTime());
        entity.setUserId(userId);
        return entity;
    }
    
    /**
     * Convert RecipeEntity to RecipeResponseDTO.
     */
    public RecipeResponseDTO toResponse(RecipeEntity entity) {
        return toResponse(entity, null);
    }
    
    /**
     * Convert RecipeEntity to RecipeResponseDTO with like info for a specific user.
     */
    public RecipeResponseDTO toResponse(RecipeEntity entity, Long currentUserId) {
        RecipeResponseDTO dto = new RecipeResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setImageUrl(entity.getImageUrl());
        dto.setServings(entity.getServings());
        dto.setCookingTime(entity.getCookingTime());
        dto.setUserId(entity.getUserId());
        
        // Set user info if available
        if (entity.getUser() != null) {
            dto.setUserName(entity.getUser().getFullName());
            dto.setUserAvatar(entity.getUser().getAvatarUrl());
        }
        
        // Convert ingredients
        if (entity.getIngredients() != null) {
            dto.setIngredients(entity.getIngredients().stream()
                .map(this::ingredientToDTO)
                .collect(Collectors.toList()));
        }
        
        // Convert steps
        if (entity.getSteps() != null) {
            dto.setSteps(entity.getSteps().stream()
                .map(this::stepToDTO)
                .collect(Collectors.toList()));
        }
        
        // Set like info
        dto.setLikesCount(entity.getLikesCount() != null ? entity.getLikesCount() : 0);
        if (currentUserId != null) {
            dto.setIsLikedByCurrentUser(recipeLikeService.isLikedByUser(currentUserId, entity.getId()));
        } else {
            dto.setIsLikedByCurrentUser(false);
        }
        
        // Set bookmark info
        dto.setBookmarksCount(entity.getBookmarksCount() != null ? entity.getBookmarksCount() : 0);
        if (currentUserId != null) {
            dto.setIsBookmarkedByCurrentUser(recipeBookmarkService.isBookmarkedByUser(currentUserId, entity.getId()));
        } else {
            dto.setIsBookmarkedByCurrentUser(false);
        }
        
        // Set rating info
        dto.setAverageRating(entity.getAverageRating() != null ? entity.getAverageRating() : 0.0);
        dto.setRatingsCount(entity.getRatingsCount() != null ? entity.getRatingsCount() : 0);
        if (currentUserId != null) {
            recipeRatingService.getUserRating(currentUserId, entity.getId())
                    .ifPresent(rating -> dto.setUserRating(rating.getRating()));
        }
        
        // Set comments count
        dto.setCommentsCount(entity.getCommentsCount() != null ? entity.getCommentsCount() : 0);
        
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * Convert IngredientDTO to IngredientEntity.
     */
    public IngredientEntity ingredientToEntity(IngredientDTO dto, Long recipeId) {
        IngredientEntity entity = new IngredientEntity();
        entity.setId(dto.getId());
        entity.setRecipeId(recipeId);
        entity.setName(dto.getName());
        entity.setQuantity(dto.getQuantity());
        entity.setUnit(dto.getUnit());
        return entity;
    }
    
    /**
     * Convert IngredientEntity to IngredientDTO.
     */
    public IngredientDTO ingredientToDTO(IngredientEntity entity) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setQuantity(entity.getQuantity());
        dto.setUnit(entity.getUnit());
        return dto;
    }
    
    /**
     * Convert RecipeStepDTO to RecipeStepEntity.
     */
    public RecipeStepEntity stepToEntity(RecipeStepDTO dto, Long recipeId) {
        RecipeStepEntity entity = new RecipeStepEntity();
        entity.setId(dto.getId());
        entity.setRecipeId(recipeId);
        entity.setStepNumber(dto.getStepNumber());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        return entity;
    }
    
    /**
     * Convert RecipeStepEntity to RecipeStepDTO.
     */
    public RecipeStepDTO stepToDTO(RecipeStepEntity entity) {
        RecipeStepDTO dto = new RecipeStepDTO();
        dto.setId(entity.getId());
        dto.setStepNumber(entity.getStepNumber());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        
        // Convert images
        if (entity.getImages() != null) {
            dto.setImages(entity.getImages().stream()
                .map(this::imageToDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    /**
     * Convert StepImageDTO to StepImageEntity.
     */
    public StepImageEntity imageToEntity(StepImageDTO dto, Long stepId) {
        StepImageEntity entity = new StepImageEntity();
        entity.setId(dto.getId());
        entity.setStepId(stepId);
        entity.setImageUrl(dto.getImageUrl());
        entity.setOrderNumber(dto.getOrderNumber());
        return entity;
    }
    
    /**
     * Convert StepImageEntity to StepImageDTO.
     */
    public StepImageDTO imageToDTO(StepImageEntity entity) {
        StepImageDTO dto = new StepImageDTO();
        dto.setId(entity.getId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setOrderNumber(entity.getOrderNumber());
        return dto;
    }
    
    /**
     * Update existing RecipeEntity with data from RecipeRequestDTO.
     */
    public void updateEntity(RecipeEntity entity, RecipeRequestDTO dto) {
        entity.setTitle(dto.getTitle());
        entity.setImageUrl(dto.getImageUrl());
        entity.setServings(dto.getServings());
        entity.setCookingTime(dto.getCookingTime());
    }
}
