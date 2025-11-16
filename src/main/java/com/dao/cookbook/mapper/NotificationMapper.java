package com.dao.cookbook.mapper;

import com.dao.cookbook.dto.response.NotificationResponseDTO;
import com.dao.cookbook.entity.NotificationEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Notification entities and DTOs.
 */
@Component
public class NotificationMapper {
    
    /**
     * Convert NotificationEntity to NotificationResponseDTO.
     */
    public NotificationResponseDTO toResponse(NotificationEntity entity) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setType(entity.getType());
        dto.setActorId(entity.getActorId());
        
        // Set actor info if available
        if (entity.getActor() != null) {
            dto.setActorName(entity.getActor().getFullName());
            dto.setActorAvatar(entity.getActor().getAvatarUrl());
        }
        
        // Set recipe info if available
        dto.setRecipeId(entity.getRecipeId());
        if (entity.getRecipe() != null) {
            dto.setRecipeTitle(entity.getRecipe().getTitle());
            dto.setRecipeImage(entity.getRecipe().getImageUrl());
        }
        
        dto.setCommentId(entity.getCommentId());
        dto.setMessage(entity.getMessage());
        dto.setIsRead(entity.getIsRead());
        dto.setCreatedAt(entity.getCreatedAt());
        
        return dto;
    }
}
