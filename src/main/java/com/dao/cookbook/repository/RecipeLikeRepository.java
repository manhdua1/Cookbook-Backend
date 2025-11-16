package com.dao.cookbook.repository;

import com.dao.cookbook.entity.RecipeLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RecipeLikeEntity operations.
 */
@Repository
public interface RecipeLikeRepository extends JpaRepository<RecipeLikeEntity, Long> {
    
    /**
     * Find a like by user ID and recipe ID.
     * 
     * @param userId the ID of the user
     * @param recipeId the ID of the recipe
     * @return Optional containing the like if found
     */
    Optional<RecipeLikeEntity> findByUserIdAndRecipeId(Long userId, Long recipeId);
    
    /**
     * Check if a user has liked a recipe.
     * 
     * @param userId the ID of the user
     * @param recipeId the ID of the recipe
     * @return true if the user has liked the recipe, false otherwise
     */
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    
    /**
     * Count likes for a specific recipe.
     * 
     * @param recipeId the ID of the recipe
     * @return number of likes
     */
    long countByRecipeId(Long recipeId);
    
    /**
     * Find all likes by a user.
     * 
     * @param userId the ID of the user
     * @return list of likes
     */
    List<RecipeLikeEntity> findByUserId(Long userId);
    
    /**
     * Find all likes for a recipe.
     * 
     * @param recipeId the ID of the recipe
     * @return list of likes
     */
    List<RecipeLikeEntity> findByRecipeId(Long recipeId);
    
    /**
     * Delete a like by user ID and recipe ID.
     * 
     * @param userId the ID of the user
     * @param recipeId the ID of the recipe
     */
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
    
    /**
     * Get recipe IDs that a user has liked.
     * 
     * @param userId the ID of the user
     * @return list of recipe IDs
     */
    @Query("SELECT rl.recipeId FROM RecipeLikeEntity rl WHERE rl.userId = :userId")
    List<Long> findRecipeIdsByUserId(@Param("userId") Long userId);
}
