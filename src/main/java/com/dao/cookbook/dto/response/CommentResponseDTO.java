package com.dao.cookbook.dto.response;

import lombok.Data;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for comment response.
 */
@Data
public class CommentResponseDTO {
    
    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private Long recipeId;
    
    private String comment;
    
    private Long parentCommentId;
    
    private List<CommentResponseDTO> replies = new ArrayList<>();
    
    private Timestamp createdAt;
    
    private Timestamp updatedAt;
}
