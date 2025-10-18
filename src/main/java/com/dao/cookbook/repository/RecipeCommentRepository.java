package com.dao.cookbook.repository;

import com.dao.cookbook.entity.RecipeCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for RecipeCommentEntity.
 * Provides CRUD operations and custom queries for recipe comments.
 */
@Repository
public interface RecipeCommentRepository extends JpaRepository<RecipeCommentEntity, Long> {
    
    /**
     * Find all comments for a recipe, ordered by creation date (newest first).
     */
    List<RecipeCommentEntity> findByRecipeIdOrderByCreatedAtDesc(Long recipeId);
    
    /**
     * Find all comments by a user.
     */
    List<RecipeCommentEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find all replies to a parent comment.
     */
    List<RecipeCommentEntity> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);
    
    /**
     * Find all root comments (comments without parent) for a recipe.
     */
    List<RecipeCommentEntity> findByRecipeIdAndParentCommentIdIsNullOrderByCreatedAtDesc(Long recipeId);
    
    /**
     * Count total comments for a recipe.
     */
    long countByRecipeId(Long recipeId);
    
    /**
     * Count comments by a user.
     */
    long countByUserId(Long userId);
    
    /**
     * Delete all comments for a recipe.
     */
    void deleteByRecipeId(Long recipeId);
    
    /**
     * Find comments with user info for a recipe (using JOIN FETCH).
     */
    @Query("SELECT c FROM RecipeCommentEntity c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.recipeId = :recipeId AND c.parentCommentId IS NULL " +
           "ORDER BY c.createdAt DESC")
    List<RecipeCommentEntity> findByRecipeIdWithUser(@Param("recipeId") Long recipeId);
}
