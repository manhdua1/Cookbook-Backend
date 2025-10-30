package com.dao.cookbook.service;

import com.dao.cookbook.entity.RecipeBookmarkEntity;
import com.dao.cookbook.entity.RecipeEntity;
import com.dao.cookbook.repository.RecipeBookmarkRepository;
import com.dao.cookbook.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for Recipe Bookmark business logic.
 */
@Service
public class RecipeBookmarkService {
    
    private final RecipeBookmarkRepository bookmarkRepository;
    private final RecipeRepository recipeRepository;
    private final NotificationService notificationService;
    
    public RecipeBookmarkService(RecipeBookmarkRepository bookmarkRepository, 
                                RecipeRepository recipeRepository,
                                NotificationService notificationService) {
        this.bookmarkRepository = bookmarkRepository;
        this.recipeRepository = recipeRepository;
        this.notificationService = notificationService;
    }
    
    /**
     * Bookmark a recipe.
     * Returns true if successfully bookmarked, false if already bookmarked.
     */
    @Transactional
    public boolean bookmarkRecipe(Long userId, Long recipeId) {
        // Check if already bookmarked
        if (bookmarkRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            return false;
        }
        
        // Verify recipe exists
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + recipeId));
        
        // Create bookmark
        RecipeBookmarkEntity bookmark = new RecipeBookmarkEntity();
        bookmark.setUserId(userId);
        bookmark.setRecipeId(recipeId);
        bookmarkRepository.save(bookmark);
        
        // Increment bookmarks count
        recipe.setBookmarksCount(recipe.getBookmarksCount() == null ? 1 : recipe.getBookmarksCount() + 1);
        recipeRepository.save(recipe);
        
        // Create notification
        try {
            notificationService.createBookmarkNotification(recipeId, userId);
        } catch (Exception e) {
            System.err.println("Failed to create bookmark notification: " + e.getMessage());
        }
        
        return true;
    }
    
    /**
     * Remove bookmark from a recipe.
     * Returns true if successfully removed, false if wasn't bookmarked.
     */
    @Transactional
    public boolean unbookmarkRecipe(Long userId, Long recipeId) {
        // Check if bookmark exists
        if (!bookmarkRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            return false;
        }
        
        // Find recipe
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + recipeId));
        
        // Delete bookmark
        bookmarkRepository.deleteByUserIdAndRecipeId(userId, recipeId);
        
        // Decrement bookmarks count (ensure it doesn't go below 0)
        int currentCount = recipe.getBookmarksCount() == null ? 0 : recipe.getBookmarksCount();
        recipe.setBookmarksCount(Math.max(0, currentCount - 1));
        recipeRepository.save(recipe);
        
        return true;
    }
    
    /**
     * Toggle bookmark status (bookmark if not bookmarked, unbookmark if bookmarked).
     * Returns true if bookmarked, false if unbookmarked.
     */
    @Transactional
    public boolean toggleBookmark(Long userId, Long recipeId) {
        if (bookmarkRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            unbookmarkRecipe(userId, recipeId);
            return false;
        } else {
            bookmarkRecipe(userId, recipeId);
            return true;
        }
    }
    
    /**
     * Check if user has bookmarked a recipe.
     */
    public boolean isBookmarkedByUser(Long userId, Long recipeId) {
        return bookmarkRepository.existsByUserIdAndRecipeId(userId, recipeId);
    }
    
    /**
     * Get total bookmarks count for a recipe.
     */
    public long getBookmarksCount(Long recipeId) {
        return bookmarkRepository.countByRecipeId(recipeId);
    }
    
    /**
     * Get all recipe IDs bookmarked by a user.
     */
    public List<Long> getBookmarkedRecipeIds(Long userId) {
        return bookmarkRepository.findRecipeIdsByUserId(userId);
    }
    
    /**
     * Get all bookmarks for a recipe.
     */
    public List<RecipeBookmarkEntity> getRecipeBookmarks(Long recipeId) {
        return bookmarkRepository.findByRecipeId(recipeId);
    }
}
