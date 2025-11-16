/**
 * Entity class representing a user in the system.
 * <p>
 * Maps to the "users" table in the database.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the user (UUID, primary key).</li>
 *   <li><b>mail</b>: User's email address (unique, not null).</li>
 *   <li><b>password</b>: User's hashed password.</li>
 *   <li><b>fullName</b>: User's full name (not null).</li>
 *   <li><b>avatarUrl</b>: URL to the user's avatar image.</li>
 *   <li><b>bio</b>: User's biography or description.</li>
 *   <li><b>hometown</b>: User's hometown.</li>
 *   <li><b>provider</b>: Authentication provider (default: "local"; possible values: "local", "google", "otp").<br>
 *       <i>Possible values: "local", "google", "otp"</i>
 *   </li>
 *   <li><b>followersCount</b>: Number of followers the user has.</li>
 *   <li><b>followingCount</b>: Number of users this user is following.</li>
 *   <li><b>createdAt</b>: Timestamp when the user was created (set by the database).</li>
 *   <li><b>updatedAt</b>: Timestamp when the user was last updated (set by the database).</li>
 * </ul>
 *
 * Uses Lombok's {@code @Data} for boilerplate code generation.
 */

package com.dao.cookbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;


    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Hashed password

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String bio;
    private String hometown;

    @Column(nullable = false)
    private String provider = "local"; // Possible values: "local", "google", "otp"

    @Column(name = "followers_count")
    private int followersCount;

    @Column(name = "following_count")
    private int followingCount;

    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.sql.Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.sql.Timestamp updatedAt;
}
