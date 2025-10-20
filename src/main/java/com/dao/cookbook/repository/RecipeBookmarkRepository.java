package com.dao.cookbook.repository;

import com.dao.cookbook.entity.RecipeBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RecipeBookmarkEntity.
 * Provides CRUD operations and custom queries for recipe bookmarks.
 */
@Repository
public interface RecipeBookmarkRepository extends JpaRepository<RecipeBookmarkEntity, Long> {
    
    /**
     * Find a bookmark by user ID and recipe ID.
     */
    Optional<RecipeBookmarkEntity> findByUserIdAndRecipeId(Long userId, Long recipeId);
    
    /**
     * Check if a user has bookmarked a recipe.
     */
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    
    /**
     * Count total bookmarks for a recipe.
     */
    long countByRecipeId(Long recipeId);
    
    /**
     * Delete a bookmark by user ID and recipe ID.
     */
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
    
    /**
     * Get all recipe IDs bookmarked by a user.
     */
    @Query("SELECT rb.recipeId FROM RecipeBookmarkEntity rb WHERE rb.userId = :userId")
    List<Long> findRecipeIdsByUserId(@Param("userId") Long userId);
    
    /**
     * Get all bookmarks for a recipe.
     */
    List<RecipeBookmarkEntity> findByRecipeId(Long recipeId);
    
    /**
     * Get all bookmarks by a user.
     */
    List<RecipeBookmarkEntity> findByUserId(Long userId);
}
