package com.dao.cookbook.service;

import com.dao.cookbook.entity.RecipeEntity;
import com.dao.cookbook.entity.RecipeLikeEntity;
import com.dao.cookbook.repository.RecipeLikeRepository;
import com.dao.cookbook.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for Recipe Like business logic.
 */
@Service
public class RecipeLikeService {

    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;
    private final NotificationService notificationService;

    public RecipeLikeService(RecipeLikeRepository recipeLikeRepository,
                            RecipeRepository recipeRepository,
                            NotificationService notificationService) {
        this.recipeLikeRepository = recipeLikeRepository;
        this.recipeRepository = recipeRepository;
        this.notificationService = notificationService;
    }

    /**
     * Like a recipe.
     * 
     * @param userId the ID of the user
     * @param recipeId the ID of the recipe
     * @return true if liked successfully, false if already liked
     */
    @Transactional
    public boolean likeRecipe(Long userId, Long recipeId) {
        // Check if recipe exists
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + recipeId));

        // Check if already liked
        if (recipeLikeRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            return false; // Already liked
        }

        // Create new like
        RecipeLikeEntity like = new RecipeLikeEntity();
        like.setUserId(userId);
        like.setRecipeId(recipeId);
        recipeLikeRepository.save(like);

        // Update likes count
        recipe.setLikesCount(recipe.getLikesCount() + 1);
        recipeRepository.save(recipe);

        // Create notification
        try {
            notificationService.createLikeNotification(recipeId, userId);
        } catch (Exception e) {
            // Log but don't fail the like operation
            System.err.println("Failed to create like notification: " + e.getMessage());
        }

        return true;
    }

    /**
     * Unlike a recipe.
     * 
     * @param userId the ID of the user
     * @param recipeId the ID of the recipe
     * @return true if unliked successfully, false if not liked before
     */
    @Transactional
    public boolean unlikeRecipe(Long userId, Long recipeId) {
        // Check if recipe exists
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + recipeId));

        // Check if liked
        if (!recipeLikeRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            return false; // Not liked before
        }

        // Delete like
        recipeLikeRepository.deleteByUserIdAndRecipeId(userId, recipeId);

        // Update likes count
        int newCount = Math.max(0, recipe.getLikesCount() - 1);
        recipe.setLikesCount(newCount);
        recipeRepository.save(recipe);

        return true;
    }

    /**
     * Toggle like/unlike a recipe.
     * 
     * @param userId the ID of the user
     * @param recipeId the ID of the recipe
     * @return true if now liked, false if now unliked
     */
    @Transactional
    public boolean toggleLike(Long userId, Long recipeId) {
        if (recipeLikeRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            unlikeRecipe(userId, recipeId);
            return false; // Now unliked
        } else {
            likeRecipe(userId, recipeId);
            return true; // Now liked
        }
    }

    /**
     * Check if a user has liked a recipe.
     * 
     * @param userId the ID of the user
     * @param recipeId the ID of the recipe
     * @return true if liked, false otherwise
     */
    public boolean isLikedByUser(Long userId, Long recipeId) {
        return recipeLikeRepository.existsByUserIdAndRecipeId(userId, recipeId);
    }

    /**
     * Get likes count for a recipe.
     * 
     * @param recipeId the ID of the recipe
     * @return number of likes
     */
    public long getLikesCount(Long recipeId) {
        return recipeLikeRepository.countByRecipeId(recipeId);
    }

    /**
     * Get all recipe IDs liked by a user.
     * 
     * @param userId the ID of the user
     * @return list of recipe IDs
     */
    public List<Long> getLikedRecipeIds(Long userId) {
        return recipeLikeRepository.findRecipeIdsByUserId(userId);
    }

    /**
     * Get all likes for a recipe.
     * 
     * @param recipeId the ID of the recipe
     * @return list of likes
     */
    public List<RecipeLikeEntity> getRecipeLikes(Long recipeId) {
        return recipeLikeRepository.findByRecipeId(recipeId);
    }
}
