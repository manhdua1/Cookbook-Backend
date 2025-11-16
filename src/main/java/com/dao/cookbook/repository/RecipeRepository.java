package com.dao.cookbook.repository;

import com.dao.cookbook.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for RecipeEntity operations.
 */
@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {
    
    /**
     * Find all recipes by user ID.
     * 
     * @param userId the ID of the user
     * @return list of recipes
     */
    List<RecipeEntity> findByUserId(Long userId);
    
    /**
     * Find recipes by title containing keyword (case-insensitive).
     * 
     * @param title the keyword to search
     * @return list of recipes
     */
    List<RecipeEntity> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find recipes from users that the current user is following.
     * Ordered by creation date descending (newest first).
     * 
     * @param userIds list of user IDs that the current user is following
     * @return list of recipes from followed users
     */
    @Query("SELECT r FROM RecipeEntity r WHERE r.userId IN :userIds ORDER BY r.createdAt DESC")
    List<RecipeEntity> findByUserIdInOrderByCreatedAtDesc(@Param("userIds") List<Long> userIds);
    
    /**
     * Find recipes that contain specific ingredients.
     * 
     * @param ingredients list of ingredient names to search for
     * @param count number of ingredients to match
     * @return list of recipes containing the specified ingredients
     */
    @Query("SELECT DISTINCT r FROM RecipeEntity r " +
           "JOIN r.ingredients i " +
           "WHERE LOWER(i.name) IN :ingredients " +
           "GROUP BY r.id " +
           "HAVING COUNT(DISTINCT i.name) >= :count")
    List<RecipeEntity> findByIngredientsContaining(
        @Param("ingredients") List<String> ingredients,
        @Param("count") long count
    );
    
    /**
     * Find recipes that do not contain specific ingredients.
     * 
     * @param ingredients list of ingredient names to exclude
     * @return list of recipes not containing the specified ingredients
     */
    @Query("SELECT r FROM RecipeEntity r " +
           "WHERE r.id NOT IN (" +
           "  SELECT DISTINCT r2.id FROM RecipeEntity r2 " +
           "  JOIN r2.ingredients i " +
           "  WHERE LOWER(i.name) IN :ingredients" +
           ")")
    List<RecipeEntity> findByIngredientsNotContaining(
        @Param("ingredients") List<String> ingredients
    );
}
