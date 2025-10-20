package com.dao.cookbook.service;

import com.dao.cookbook.entity.RecipeRatingEntity;
import com.dao.cookbook.entity.RecipeEntity;
import com.dao.cookbook.repository.RecipeRatingRepository;
import com.dao.cookbook.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for Recipe Rating business logic.
 */
@Service
public class RecipeRatingService {
    
    private final RecipeRatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    
    public RecipeRatingService(RecipeRatingRepository ratingRepository,
                              RecipeRepository recipeRepository) {
        this.ratingRepository = ratingRepository;
        this.recipeRepository = recipeRepository;
    }
    
    /**
     * Rate a recipe (add or update rating).
     */
    @Transactional
    public RecipeRatingEntity rateRecipe(Long userId, Long recipeId, Integer ratingValue) {
        // Validate rating value (1-5 stars)
        if (ratingValue < 1 || ratingValue > 5) {
            throw new RuntimeException("Đánh giá phải từ 1 đến 5 sao");
        }
        
        // Verify recipe exists
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + recipeId));
        
        // Check if user has already rated this recipe
        Optional<RecipeRatingEntity> existingRating = ratingRepository.findByUserIdAndRecipeId(userId, recipeId);
        
        RecipeRatingEntity rating;
        boolean isNewRating = existingRating.isEmpty();
        
        if (existingRating.isPresent()) {
            // Update existing rating
            rating = existingRating.get();
            rating.setRating(ratingValue);
        } else {
            // Create new rating
            rating = new RecipeRatingEntity();
            rating.setUserId(userId);
            rating.setRecipeId(recipeId);
            rating.setRating(ratingValue);
        }
        
        RecipeRatingEntity savedRating = ratingRepository.save(rating);
        
        // Update recipe's average rating and ratings count
        updateRecipeRatingStats(recipe, isNewRating);
        
        return savedRating;
    }
    
    /**
     * Delete a rating.
     */
    @Transactional
    public void deleteRating(Long userId, Long recipeId) {
        // Check if rating exists
        if (!ratingRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            throw new RuntimeException("Bạn chưa đánh giá công thức này");
        }
        
        // Find recipe
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + recipeId));
        
        // Delete rating
        ratingRepository.deleteByUserIdAndRecipeId(userId, recipeId);
        
        // Update recipe's rating stats
        updateRecipeRatingStats(recipe, false);
    }
    
    /**
     * Get user's rating for a recipe.
     */
    public Optional<RecipeRatingEntity> getUserRating(Long userId, Long recipeId) {
        return ratingRepository.findByUserIdAndRecipeId(userId, recipeId);
    }
    
    /**
     * Check if user has rated a recipe.
     */
    public boolean hasUserRated(Long userId, Long recipeId) {
        return ratingRepository.existsByUserIdAndRecipeId(userId, recipeId);
    }
    
    /**
     * Get average rating for a recipe.
     */
    public Double getAverageRating(Long recipeId) {
        Double avg = ratingRepository.calculateAverageRating(recipeId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }
    
    /**
     * Get ratings count for a recipe.
     */
    public long getRatingsCount(Long recipeId) {
        return ratingRepository.countByRecipeId(recipeId);
    }
    
    /**
     * Get rating distribution (count for each star rating 1-5).
     */
    public Map<Integer, Long> getRatingDistribution(Long recipeId) {
        List<Object[]> distribution = ratingRepository.getRatingDistribution(recipeId);
        Map<Integer, Long> result = new HashMap<>();
        
        // Initialize with 0 counts
        for (int i = 1; i <= 5; i++) {
            result.put(i, 0L);
        }
        
        // Fill in actual counts
        for (Object[] row : distribution) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];
            result.put(rating, count);
        }
        
        return result;
    }
    
    /**
     * Get all ratings for a recipe.
     */
    public List<RecipeRatingEntity> getRecipeRatings(Long recipeId) {
        return ratingRepository.findByRecipeIdOrderByCreatedAtDesc(recipeId);
    }
    
    /**
     * Update recipe's rating statistics (average rating and count).
     */
    private void updateRecipeRatingStats(RecipeEntity recipe, boolean isNewRating) {
        // Calculate new average rating
        Double avgRating = ratingRepository.calculateAverageRating(recipe.getId());
        recipe.setAverageRating(avgRating != null ? Math.round(avgRating * 100.0) / 100.0 : 0.0);
        
        // Update ratings count
        if (isNewRating) {
            recipe.setRatingsCount(recipe.getRatingsCount() == null ? 1 : recipe.getRatingsCount() + 1);
        } else {
            // For delete, recalculate count
            long count = ratingRepository.countByRecipeId(recipe.getId());
            recipe.setRatingsCount((int) count);
        }
        
        recipeRepository.save(recipe);
    }
}
