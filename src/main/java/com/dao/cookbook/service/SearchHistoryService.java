package com.dao.cookbook.service;

import com.dao.cookbook.entity.SearchHistoryEntity;
import com.dao.cookbook.repository.SearchHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for SearchHistory business logic.
 */
@Service
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    /**
     * Save a search query to user's history.
     * This will create a new entry even if the query already exists (to track frequency).
     */
    @Transactional
    public SearchHistoryEntity saveSearchHistory(Long userId, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query không được để trống");
        }
        
        SearchHistoryEntity searchHistory = new SearchHistoryEntity();
        searchHistory.setUserId(userId);
        searchHistory.setSearchQuery(searchQuery.trim());
        
        return searchHistoryRepository.save(searchHistory);
    }

    /**
     * Get all search history for a user, ordered by most recent first.
     */
    public List<SearchHistoryEntity> getUserSearchHistory(Long userId) {
        return searchHistoryRepository.findByUserIdOrderBySearchedAtDesc(userId);
    }

    /**
     * Get distinct search queries for a user (no duplicates), ordered by most recent.
     * This is useful for showing unique search suggestions.
     */
    public List<String> getUniqueSearchQueries(Long userId) {
        return searchHistoryRepository.findDistinctSearchQueriesByUserId(userId);
    }

    /**
     * Get limited number of unique search queries.
     */
    public List<String> getRecentUniqueSearchQueries(Long userId, int limit) {
        List<String> allQueries = searchHistoryRepository.findDistinctSearchQueriesByUserId(userId);
        return allQueries.stream()
                .limit(limit)
                .toList();
    }

    /**
     * Clear all search history for a user.
     */
    @Transactional
    public void clearUserSearchHistory(Long userId) {
        searchHistoryRepository.deleteByUserId(userId);
    }

    /**
     * Delete a specific search query from user's history.
     */
    @Transactional
    public void deleteSearchQuery(Long userId, String searchQuery) {
        searchHistoryRepository.deleteByUserIdAndSearchQuery(userId, searchQuery);
    }

    /**
     * Get total number of searches by user.
     */
    public long getUserSearchCount(Long userId) {
        return searchHistoryRepository.countByUserId(userId);
    }
}
