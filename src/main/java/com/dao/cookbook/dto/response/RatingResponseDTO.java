package com.dao.cookbook.dto.response;

import lombok.Data;
import java.sql.Timestamp;

/**
 * DTO for rating response.
 */
@Data
public class RatingResponseDTO {
    
    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private Long recipeId;
    
    private Integer rating;
    
    private Timestamp createdAt;
    
    private Timestamp updatedAt;
}
