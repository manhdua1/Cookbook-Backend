package com.dao.cookbook.service;

import com.dao.cookbook.dto.response.RecipeResponseDTO;
import com.dao.cookbook.entity.RecipeEntity;
import com.dao.cookbook.entity.RecipeViewHistoryEntity;
import com.dao.cookbook.mapper.RecipeMapper;
import com.dao.cookbook.repository.RecipeRepository;
import com.dao.cookbook.repository.RecipeViewHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Recipe View History business logic.
 */
@Service
public class RecipeViewHistoryService {

    private final RecipeViewHistoryRepository viewHistoryRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public RecipeViewHistoryService(RecipeViewHistoryRepository viewHistoryRepository,
                                   RecipeRepository recipeRepository,
                                   RecipeMapper recipeMapper) {
        this.viewHistoryRepository = viewHistoryRepository;
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
    }

    /**
     * Save or update view history when user views a recipe.
     * Updates viewedAt timestamp if user already viewed this recipe before.
     * 
     * @param userId the ID of the user viewing the recipe
     * @param recipeId the ID of the recipe being viewed
     */
    @Transactional
    public void saveViewHistory(Long userId, Long recipeId) {
        // Check if recipe exists
        if (!recipeRepository.existsById(recipeId)) {
            return; // Silently return if recipe doesn't exist
        }

        // Check if user has viewed this recipe before
        Optional<RecipeViewHistoryEntity> existingView = 
            viewHistoryRepository.findFirstByUserIdAndRecipeIdOrderByViewedAtDesc(userId, recipeId);

        if (existingView.isPresent()) {
            // Update existing entry by creating new one (to update viewedAt)
            RecipeViewHistoryEntity newView = new RecipeViewHistoryEntity();
            newView.setUserId(userId);
            newView.setRecipeId(recipeId);
            viewHistoryRepository.save(newView);
        } else {
            // Create new entry
            RecipeViewHistoryEntity viewHistory = new RecipeViewHistoryEntity();
            viewHistory.setUserId(userId);
            viewHistory.setRecipeId(recipeId);
            viewHistoryRepository.save(viewHistory);
        }
    }

    /**
     * Get recently viewed recipes by user.
     * Returns distinct recipes (most recent view of each recipe).
     * 
     * @param userId the ID of the user
     * @param limit maximum number of recipes to return (default: 20)
     * @return list of recently viewed recipes
     */
    public List<RecipeResponseDTO> getRecentlyViewedRecipes(Long userId, Integer limit) {
        int pageSize = (limit != null && limit > 0) ? limit : 20;
        
        List<RecipeViewHistoryEntity> viewHistory = 
            viewHistoryRepository.findRecentlyViewedByUserIdWithLimit(userId, PageRequest.of(0, pageSize));
        
        return viewHistory.stream()
                .map(vh -> {
                    RecipeEntity recipe = recipeRepository.findById(vh.getRecipeId()).orElse(null);
                    if (recipe != null) {
                        return recipeMapper.toResponse(recipe, userId);
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Get all recently viewed recipes (no limit).
     */
    public List<RecipeResponseDTO> getRecentlyViewedRecipes(Long userId) {
        List<RecipeViewHistoryEntity> viewHistory = 
            viewHistoryRepository.findRecentlyViewedByUserId(userId);
        
        return viewHistory.stream()
                .map(vh -> {
                    RecipeEntity recipe = recipeRepository.findById(vh.getRecipeId()).orElse(null);
                    if (recipe != null) {
                        return recipeMapper.toResponse(recipe, userId);
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Count how many times a user has viewed a specific recipe.
     */
    public long countUserViews(Long userId, Long recipeId) {
        return viewHistoryRepository.countByUserIdAndRecipeId(userId, recipeId);
    }

    /**
     * Clear all view history for a user.
     */
    @Transactional
    public void clearUserViewHistory(Long userId) {
        viewHistoryRepository.deleteByUserId(userId);
    }

    /**
     * Remove a specific recipe from user's view history.
     */
    @Transactional
    public void removeRecipeFromHistory(Long userId, Long recipeId) {
        viewHistoryRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }
}
