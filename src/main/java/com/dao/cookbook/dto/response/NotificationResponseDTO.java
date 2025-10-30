package com.dao.cookbook.dto.response;

import lombok.Data;
import java.sql.Timestamp;

/**
 * DTO for Notification response.
 */
@Data
public class NotificationResponseDTO {
    private Long id;
    private Long userId;
    private String type;
    private Long actorId;
    private String actorName;
    private String actorAvatar;
    private Long recipeId;
    private String recipeTitle;
    private String recipeImage;
    private Long commentId;
    private String message;
    private Boolean isRead;
    private Timestamp createdAt;
}
