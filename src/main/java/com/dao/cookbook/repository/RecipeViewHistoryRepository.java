package com.dao.cookbook.repository;

import com.dao.cookbook.entity.RecipeViewHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeViewHistoryRepository extends JpaRepository<RecipeViewHistoryEntity, Long> {
    
    /**
     * Find the most recent view history entry for a specific user and recipe.
     * Used to update existing entry instead of creating duplicate.
     */
    Optional<RecipeViewHistoryEntity> findFirstByUserIdAndRecipeIdOrderByViewedAtDesc(Long userId, Long recipeId);
    
    /**
     * Get recently viewed recipes by user, ordered by most recent first.
     * Returns distinct recipes (most recent view of each recipe).
     * 
     * @param userId the ID of the user
     * @return list of view history entries
     */
    @Query("SELECT vh FROM RecipeViewHistoryEntity vh WHERE vh.userId = :userId " +
           "AND vh.id IN (SELECT MAX(vh2.id) FROM RecipeViewHistoryEntity vh2 " +
           "WHERE vh2.userId = :userId GROUP BY vh2.recipeId) " +
           "ORDER BY vh.viewedAt DESC")
    List<RecipeViewHistoryEntity> findRecentlyViewedByUserId(@Param("userId") Long userId);
    
    /**
     * Get recently viewed recipes with limit using Pageable.
     */
    @Query("SELECT vh FROM RecipeViewHistoryEntity vh WHERE vh.userId = :userId " +
           "AND vh.id IN (SELECT MAX(vh2.id) FROM RecipeViewHistoryEntity vh2 " +
           "WHERE vh2.userId = :userId GROUP BY vh2.recipeId) " +
           "ORDER BY vh.viewedAt DESC")
    List<RecipeViewHistoryEntity> findRecentlyViewedByUserIdWithLimit(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);
    
    /**
     * Count total views for a recipe by a specific user.
     */
    long countByUserIdAndRecipeId(Long userId, Long recipeId);
    
    /**
     * Delete all view history for a user.
     */
    @Modifying
    @Query("DELETE FROM RecipeViewHistoryEntity vh WHERE vh.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    /**
     * Delete view history for a specific recipe by a user.
     */
    @Modifying
    @Query("DELETE FROM RecipeViewHistoryEntity vh WHERE vh.userId = :userId AND vh.recipeId = :recipeId")
    void deleteByUserIdAndRecipeId(@Param("userId") Long userId, @Param("recipeId") Long recipeId);
}
