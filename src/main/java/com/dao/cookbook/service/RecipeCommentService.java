package com.dao.cookbook.service;

import com.dao.cookbook.entity.RecipeCommentEntity;
import com.dao.cookbook.entity.RecipeEntity;
import com.dao.cookbook.repository.RecipeCommentRepository;
import com.dao.cookbook.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for Recipe Comment business logic.
 */
@Service
public class RecipeCommentService {
    
    private final RecipeCommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final NotificationService notificationService;
    
    public RecipeCommentService(RecipeCommentRepository commentRepository,
                               RecipeRepository recipeRepository,
                               @org.springframework.context.annotation.Lazy NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.recipeRepository = recipeRepository;
        this.notificationService = notificationService;
    }
    
    /**
     * Add a comment to a recipe.
     */
    @Transactional
    public RecipeCommentEntity addComment(Long userId, Long recipeId, String commentText, Long parentCommentId) {
        // Verify recipe exists
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + recipeId));
        
        // If it's a reply, verify parent comment exists
        if (parentCommentId != null) {
            commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận cha với ID: " + parentCommentId));
        }
        
        // Create comment
        RecipeCommentEntity comment = new RecipeCommentEntity();
        comment.setUserId(userId);
        comment.setRecipeId(recipeId);
        comment.setComment(commentText);
        comment.setParentCommentId(parentCommentId);
        
        RecipeCommentEntity savedComment = commentRepository.save(comment);
        
        // Increment comments count (only for root comments, not replies)
        if (parentCommentId == null) {
            recipe.setCommentsCount(recipe.getCommentsCount() == null ? 1 : recipe.getCommentsCount() + 1);
            recipeRepository.save(recipe);
        }
        
        // Create notification
        try {
            if (parentCommentId == null) {
                // Notification for recipe owner when someone comments
                notificationService.createCommentNotification(recipeId, userId, savedComment.getId());
            } else {
                // Notification for parent comment owner when someone replies
                RecipeCommentEntity parentComment = commentRepository.findById(parentCommentId).orElse(null);
                if (parentComment != null) {
                    notificationService.createReplyNotification(
                        parentComment.getUserId(), 
                        userId, 
                        recipeId, 
                        savedComment.getId(), 
                        recipe.getTitle()
                    );
                }
            }
        } catch (Exception e) {
            // Don't fail the comment operation if notification fails
            System.err.println("Failed to create comment notification: " + e.getMessage());
        }
        
        return savedComment;
    }
    
    /**
     * Update a comment.
     */
    @Transactional
    public RecipeCommentEntity updateComment(Long commentId, Long userId, String newCommentText) {
        RecipeCommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + commentId));
        
        // Check if user owns this comment
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa bình luận này");
        }
        
        comment.setComment(newCommentText);
        return commentRepository.save(comment);
    }
    
    /**
     * Delete a comment.
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        RecipeCommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + commentId));
        
        // Check if user owns this comment
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa bình luận này");
        }
        
        // Find recipe
        RecipeEntity recipe = recipeRepository.findById(comment.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức"));
        
        // Delete comment (cascade will delete replies)
        commentRepository.delete(comment);
        
        // Decrement comments count (only for root comments)
        if (comment.getParentCommentId() == null) {
            int currentCount = recipe.getCommentsCount() == null ? 0 : recipe.getCommentsCount();
            recipe.setCommentsCount(Math.max(0, currentCount - 1));
            recipeRepository.save(recipe);
        }
    }
    
    /**
     * Get all comments for a recipe (root comments only).
     */
    public List<RecipeCommentEntity> getRecipeComments(Long recipeId) {
        return commentRepository.findByRecipeIdWithUser(recipeId);
    }
    
    /**
     * Get all replies to a comment.
     */
    public List<RecipeCommentEntity> getCommentReplies(Long parentCommentId) {
        return commentRepository.findByParentCommentIdOrderByCreatedAtAsc(parentCommentId);
    }
    
    /**
     * Get comments count for a recipe.
     */
    public long getCommentsCount(Long recipeId) {
        return commentRepository.countByRecipeId(recipeId);
    }
    
    /**
     * Get a comment by ID.
     */
    public RecipeCommentEntity getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + commentId));
    }
}
