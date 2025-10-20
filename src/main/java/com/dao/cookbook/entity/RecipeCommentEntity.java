package com.dao.cookbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

/**
 * Entity representing a comment on a recipe.
 * Users can leave text comments on recipes.
 */
@Entity
@Table(name = "recipe_comments")
@Data
public class RecipeCommentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;
    
    @Column(name = "comment", columnDefinition = "TEXT", nullable = false)
    private String comment;
    
    @Column(name = "parent_comment_id")
    private Long parentCommentId; // For nested comments/replies
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
    
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    private RecipeEntity recipe;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", insertable = false, updatable = false)
    private RecipeCommentEntity parentComment;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
