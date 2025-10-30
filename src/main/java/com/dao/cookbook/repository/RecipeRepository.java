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
}
