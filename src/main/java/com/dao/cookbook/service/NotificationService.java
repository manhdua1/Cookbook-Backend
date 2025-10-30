package com.dao.cookbook.service;

import com.dao.cookbook.entity.NotificationEntity;
import com.dao.cookbook.entity.RecipeEntity;
import com.dao.cookbook.entity.UserEntity;
import com.dao.cookbook.repository.NotificationRepository;
import com.dao.cookbook.repository.RecipeRepository;
import com.dao.cookbook.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for Notification business logic.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public NotificationService(NotificationRepository notificationRepository,
                              UserRepository userRepository,
                              RecipeRepository recipeRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    /**
     * Create a notification for a user.
     */
    @Transactional
    public NotificationEntity createNotification(Long userId, String type, Long actorId, 
                                                 Long recipeId, Long commentId, String message) {
        // Don't create notification if actor is the same as recipient
        if (userId.equals(actorId)) {
            return null;
        }

        // Check if similar unread notification already exists (to avoid spam)
        if (recipeId != null && notificationRepository.existsByUserIdAndTypeAndActorIdAndRecipeIdAndIsReadFalse(
                userId, type, actorId, recipeId)) {
            return null; // Don't create duplicate
        }

        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setActorId(actorId);
        notification.setRecipeId(recipeId);
        notification.setCommentId(commentId);
        notification.setMessage(message);
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    /**
     * Create notification when someone likes a recipe.
     */
    @Transactional
    public void createLikeNotification(Long recipeId, Long actorId) {
        RecipeEntity recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) return;

        UserEntity actor = userRepository.findById(actorId).orElse(null);
        if (actor == null) return;

        String message = actor.getFullName() + " đã thích công thức \"" + recipe.getTitle() + "\" của bạn";
        createNotification(recipe.getUserId(), "LIKE", actorId, recipeId, null, message);
    }

    /**
     * Create notification when someone comments on a recipe.
     */
    @Transactional
    public void createCommentNotification(Long recipeId, Long actorId, Long commentId) {
        RecipeEntity recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) return;

        UserEntity actor = userRepository.findById(actorId).orElse(null);
        if (actor == null) return;

        String message = actor.getFullName() + " đã bình luận về công thức \"" + recipe.getTitle() + "\" của bạn";
        createNotification(recipe.getUserId(), "COMMENT", actorId, recipeId, commentId, message);
    }

    /**
     * Create notification when someone replies to a comment.
     */
    @Transactional
    public void createReplyNotification(Long parentCommentUserId, Long actorId, 
                                       Long recipeId, Long commentId, String recipeTitle) {
        UserEntity actor = userRepository.findById(actorId).orElse(null);
        if (actor == null) return;

        String message = actor.getFullName() + " đã trả lời bình luận của bạn trong \"" + recipeTitle + "\"";
        createNotification(parentCommentUserId, "REPLY", actorId, recipeId, commentId, message);
    }

    /**
     * Create notification when someone rates a recipe.
     */
    @Transactional
    public void createRatingNotification(Long recipeId, Long actorId, int rating) {
        RecipeEntity recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) return;

        UserEntity actor = userRepository.findById(actorId).orElse(null);
        if (actor == null) return;

        String message = actor.getFullName() + " đã đánh giá " + rating + " sao cho công thức \"" + recipe.getTitle() + "\" của bạn";
        createNotification(recipe.getUserId(), "RATING", actorId, recipeId, null, message);
    }

    /**
     * Create notification when someone bookmarks a recipe.
     */
    @Transactional
    public void createBookmarkNotification(Long recipeId, Long actorId) {
        RecipeEntity recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) return;

        UserEntity actor = userRepository.findById(actorId).orElse(null);
        if (actor == null) return;

        String message = actor.getFullName() + " đã lưu công thức \"" + recipe.getTitle() + "\" của bạn";
        createNotification(recipe.getUserId(), "BOOKMARK", actorId, recipeId, null, message);
    }

    /**
     * Create notification when someone follows a user.
     */
    @Transactional
    public void createFollowNotification(Long followerId, Long followingId) {
        UserEntity follower = userRepository.findById(followerId).orElse(null);
        if (follower == null) return;

        String message = follower.getFullName() + " đã bắt đầu theo dõi bạn";
        createNotification(followingId, "FOLLOW", followerId, null, null, message);
    }

    /**
     * Get all notifications for a user.
     */
    public List<NotificationEntity> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get unread notifications for a user.
     */
    public List<NotificationEntity> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * Count unread notifications.
     */
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Mark a notification as read.
     */
    @Transactional
    public boolean markAsRead(Long notificationId, Long userId) {
        int updated = notificationRepository.markAsRead(notificationId, userId);
        return updated > 0;
    }

    /**
     * Mark all notifications as read.
     */
    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId);
    }

    /**
     * Delete a notification.
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        notificationRepository.deleteByIdAndUserId(notificationId, userId);
    }

    /**
     * Delete all notifications for a user.
     */
    @Transactional
    public void deleteAllNotifications(Long userId) {
        notificationRepository.deleteByUserId(userId);
    }
}
