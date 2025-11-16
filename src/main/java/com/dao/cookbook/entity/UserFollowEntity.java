package com.dao.cookbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

/**
 * Entity class representing a user follow relationship.
 * <p>
 * Maps to the "user_follows" table in the database.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the follow relationship (primary key).</li>
 *   <li><b>followerId</b>: ID of the user who follows (foreign key).</li>
 *   <li><b>followingId</b>: ID of the user being followed (foreign key).</li>
 *   <li><b>createdAt</b>: Timestamp when the follow relationship was created.</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "user_follows")
public class UserFollowEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "follower_id", nullable = false)
    private Long followerId; // User who follows

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    private UserEntity follower;

    @Column(name = "following_id", nullable = false)
    private Long followingId; // User being followed

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", insertable = false, updatable = false)
    private UserEntity following;

    @Column(name = "created_at", insertable = false, updatable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
}
