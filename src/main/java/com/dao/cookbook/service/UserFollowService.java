package com.dao.cookbook.service;

import com.dao.cookbook.entity.UserEntity;
import com.dao.cookbook.entity.UserFollowEntity;
import com.dao.cookbook.repository.UserFollowRepository;
import com.dao.cookbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFollowService {

    @Autowired
    private UserFollowRepository userFollowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * Follow a user
     */
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        // Can't follow yourself
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        // Check if both users exist
        if (!userRepository.existsById(followerId)) {
            throw new IllegalArgumentException("Follower user not found");
        }
        if (!userRepository.existsById(followingId)) {
            throw new IllegalArgumentException("Following user not found");
        }

        // Check if already following
        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new IllegalArgumentException("Already following this user");
        }

        // Create follow relationship
        UserFollowEntity follow = new UserFollowEntity();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        userFollowRepository.save(follow);

        // Create notification for the followed user
        try {
            notificationService.createFollowNotification(followerId, followingId);
        } catch (Exception e) {
            // Don't fail the follow operation if notification fails
            System.err.println("Failed to create follow notification: " + e.getMessage());
        }
    }

    /**
     * Unfollow a user
     */
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        if (!userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new IllegalArgumentException("Not following this user");
        }

        userFollowRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    /**
     * Get list of users that a specific user is following
     */
    public List<UserEntity> getFollowing(Long userId) {
        List<UserFollowEntity> follows = userFollowRepository.findByFollowerId(userId);
        List<Long> followingIds = follows.stream()
                .map(UserFollowEntity::getFollowingId)
                .collect(Collectors.toList());
        
        return userRepository.findAllById(followingIds);
    }

    /**
     * Get list of followers of a specific user
     */
    public List<UserEntity> getFollowers(Long userId) {
        List<UserFollowEntity> follows = userFollowRepository.findByFollowingId(userId);
        List<Long> followerIds = follows.stream()
                .map(UserFollowEntity::getFollowerId)
                .collect(Collectors.toList());
        
        return userRepository.findAllById(followerIds);
    }

    /**
     * Check if one user is following another
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    /**
     * Get count of users that a specific user is following
     */
    public long getFollowingCount(Long userId) {
        return userFollowRepository.countByFollowerId(userId);
    }

    /**
     * Get count of followers of a specific user
     */
    public long getFollowerCount(Long userId) {
        return userFollowRepository.countByFollowingId(userId);
    }

    /**
     * Get list of user IDs that a user is following (for feed queries)
     */
    public List<Long> getFollowingIds(Long userId) {
        return userFollowRepository.findFollowingIdsByFollowerId(userId);
    }
}
