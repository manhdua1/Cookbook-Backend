package com.dao.cookbook.repository;

import com.dao.cookbook.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
