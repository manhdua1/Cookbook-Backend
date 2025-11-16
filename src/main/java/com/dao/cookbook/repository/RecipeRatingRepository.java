package com.dao.cookbook.repository;

import com.dao.cookbook.entity.RecipeRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RecipeRatingEntity.
 * Provides CRUD operations and custom queries for recipe ratings.
 */
@Repository
public interface RecipeRatingRepository extends JpaRepository<RecipeRatingEntity, Long> {
    
    /**
     * Find a rating by user ID and recipe ID.
     */
    Optional<RecipeRatingEntity> findByUserIdAndRecipeId(Long userId, Long recipeId);
    
    /**
     * Check if a user has rated a recipe.
     */
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    
    /**
     * Find all ratings for a recipe.
     */
    List<RecipeRatingEntity> findByRecipeIdOrderByCreatedAtDesc(Long recipeId);
    
    /**
     * Find all ratings by a user.
     */
    List<RecipeRatingEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Count total ratings for a recipe.
     */
    long countByRecipeId(Long recipeId);
    
    /**
     * Calculate average rating for a recipe.
     */
    @Query("SELECT AVG(r.rating) FROM RecipeRatingEntity r WHERE r.recipeId = :recipeId")
    Double calculateAverageRating(@Param("recipeId") Long recipeId);
    
    /**
     * Get rating distribution for a recipe (count for each star rating 1-5).
     */
    @Query("SELECT r.rating, COUNT(r) FROM RecipeRatingEntity r " +
           "WHERE r.recipeId = :recipeId " +
           "GROUP BY r.rating " +
           "ORDER BY r.rating DESC")
    List<Object[]> getRatingDistribution(@Param("recipeId") Long recipeId);
    
    /**
     * Delete rating by user ID and recipe ID.
     */
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
    
    /**
     * Delete all ratings for a recipe.
     */
    void deleteByRecipeId(Long recipeId);
}
