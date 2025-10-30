package com.dao.cookbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

/**
 * Entity class representing a notification.
 * <p>
 * Maps to the "notifications" table in the database.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the notification (primary key).</li>
 *   <li><b>userId</b>: ID of the user who receives this notification (foreign key).</li>
 *   <li><b>type</b>: Type of notification (LIKE, COMMENT, RATING, REPLY, FOLLOW, etc.).</li>
 *   <li><b>actorId</b>: ID of the user who triggered this notification.</li>
 *   <li><b>recipeId</b>: ID of the related recipe (nullable).</li>
 *   <li><b>commentId</b>: ID of the related comment (nullable).</li>
 *   <li><b>message</b>: Notification message content.</li>
 *   <li><b>isRead</b>: Whether the notification has been read.</li>
 *   <li><b>createdAt</b>: Timestamp when the notification was created.</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "notifications")
public class NotificationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // LIKE, COMMENT, RATING, REPLY, BOOKMARK, etc.

    @Column(name = "actor_id", nullable = false)
    private Long actorId; // User who performed the action

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", insertable = false, updatable = false)
    private UserEntity actor;

    @Column(name = "recipe_id")
    private Long recipeId; // Related recipe (nullable)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    private RecipeEntity recipe;

    @Column(name = "comment_id")
    private Long commentId; // Related comment (nullable)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private RecipeCommentEntity comment;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead = false;

    @Column(name = "created_at", insertable = false, updatable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
}
