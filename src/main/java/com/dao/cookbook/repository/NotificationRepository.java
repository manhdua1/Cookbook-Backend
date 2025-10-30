package com.dao.cookbook.repository;

import com.dao.cookbook.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    
    /**
     * Find all notifications for a user, ordered by most recent first.
     */
    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find unread notifications for a user.
     */
    List<NotificationEntity> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    /**
     * Count unread notifications for a user.
     */
    long countByUserIdAndIsReadFalse(Long userId);
    
    /**
     * Mark a notification as read.
     */
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.id = :id AND n.userId = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * Mark all notifications as read for a user.
     */
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);
    
    /**
     * Delete all notifications for a user.
     */
    void deleteByUserId(Long userId);
    
    /**
     * Delete a specific notification.
     */
    void deleteByIdAndUserId(Long id, Long userId);
    
    /**
     * Check if a similar notification already exists (to avoid duplicates).
     */
    boolean existsByUserIdAndTypeAndActorIdAndRecipeIdAndIsReadFalse(
        Long userId, String type, Long actorId, Long recipeId);
}
