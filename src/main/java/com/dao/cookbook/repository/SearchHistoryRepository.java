package com.dao.cookbook.repository;

import com.dao.cookbook.entity.SearchHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SearchHistory entity.
 */
@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistoryEntity, Long> {
    
    /**
     * Find all search history for a user, ordered by most recent first.
     */
    List<SearchHistoryEntity> findByUserIdOrderBySearchedAtDesc(Long userId);
    
    /**
     * Find recent unique search queries for a user (no duplicates).
     * This returns distinct search queries ordered by the most recent search.
     */
    @Query("SELECT sh.searchQuery FROM SearchHistoryEntity sh " +
           "WHERE sh.userId = :userId " +
           "GROUP BY sh.searchQuery " +
           "ORDER BY MAX(sh.searchedAt) DESC")
    List<String> findDistinctSearchQueriesByUserId(@Param("userId") Long userId);
    
    /**
     * Check if a specific search query exists for a user.
     */
    Optional<SearchHistoryEntity> findFirstByUserIdAndSearchQueryOrderBySearchedAtDesc(Long userId, String searchQuery);
    
    /**
     * Delete all search history for a user.
     */
    void deleteByUserId(Long userId);
    
    /**
     * Delete a specific search query for a user.
     */
    void deleteByUserIdAndSearchQuery(Long userId, String searchQuery);
    
    /**
     * Count total searches by user.
     */
    long countByUserId(Long userId);
}
