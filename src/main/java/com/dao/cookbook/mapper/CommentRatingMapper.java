package com.dao.cookbook.mapper;

import com.dao.cookbook.dto.response.CommentResponseDTO;
import com.dao.cookbook.dto.response.RatingResponseDTO;
import com.dao.cookbook.entity.RecipeCommentEntity;
import com.dao.cookbook.entity.RecipeRatingEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Comment/Rating entities and DTOs.
 */
@Component
public class CommentRatingMapper {
    
    /**
     * Convert RecipeCommentEntity to CommentResponseDTO.
     */
    public CommentResponseDTO commentToResponse(RecipeCommentEntity entity) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setRecipeId(entity.getRecipeId());
        dto.setComment(entity.getComment());
        dto.setParentCommentId(entity.getParentCommentId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Set user info if available
        if (entity.getUser() != null) {
            dto.setUserName(entity.getUser().getFullName());
            dto.setUserAvatar(entity.getUser().getAvatarUrl());
        }
        
        return dto;
    }
    
    /**
     * Convert RecipeRatingEntity to RatingResponseDTO.
     */
    public RatingResponseDTO ratingToResponse(RecipeRatingEntity entity) {
        RatingResponseDTO dto = new RatingResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setRecipeId(entity.getRecipeId());
        dto.setRating(entity.getRating());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Set user info if available
        if (entity.getUser() != null) {
            dto.setUserName(entity.getUser().getFullName());
            dto.setUserAvatar(entity.getUser().getAvatarUrl());
        }
        
        return dto;
    }
}
