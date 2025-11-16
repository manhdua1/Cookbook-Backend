package com.dao.cookbook.repository;

import com.dao.cookbook.entity.UserFollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollowEntity, Long> {
    
    // Find all users that a specific user is following
    List<UserFollowEntity> findByFollowerId(Long followerId);
    
    // Find all followers of a specific user
    List<UserFollowEntity> findByFollowingId(Long followingId);
    
    // Check if a follow relationship exists
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    // Find specific follow relationship
    Optional<UserFollowEntity> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    // Count how many users a specific user is following
    long countByFollowerId(Long followerId);
    
    // Count how many followers a specific user has
    long countByFollowingId(Long followingId);
    
    // Delete a specific follow relationship
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    // Get list of user IDs that a user is following (for feed queries)
    @Query("SELECT f.followingId FROM UserFollowEntity f WHERE f.followerId = :followerId")
    List<Long> findFollowingIdsByFollowerId(@Param("followerId") Long followerId);
}
