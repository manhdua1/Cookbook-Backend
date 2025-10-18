package com.dao.cookbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a recipe in the system.
 * <p>
 * Maps to the "recipes" table in the database.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the recipe (primary key).</li>
 *   <li><b>title</b>: Recipe title (not null).</li>
 *   <li><b>imageUrl</b>: URL to the recipe's main image.</li>
 *   <li><b>servings</b>: Number of servings.</li>
 *   <li><b>cookingTime</b>: Cooking time in minutes.</li>
 *   <li><b>userId</b>: ID of the user who created this recipe (foreign key).</li>
 *   <li><b>user</b>: The user who owns this recipe.</li>
 *   <li><b>ingredients</b>: List of ingredients for this recipe.</li>
 *   <li><b>steps</b>: List of cooking steps for this recipe.</li>
 *   <li><b>createdAt</b>: Timestamp when the recipe was created.</li>
 *   <li><b>updatedAt</b>: Timestamp when the recipe was last updated.</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "recipes")
public class RecipeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Integer servings;

    @Column(name = "cooking_time")
    private Integer cookingTime; // in minutes

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientEntity> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStepEntity> steps = new ArrayList<>();

    @Column(name = "likes_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likesCount = 0;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeLikeEntity> likes = new ArrayList<>();

    @Column(name = "bookmarks_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer bookmarksCount = 0;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeBookmarkEntity> bookmarks = new ArrayList<>();

    @Column(name = "average_rating", columnDefinition = "DECIMAL(3,2) DEFAULT 0.00")
    private Double averageRating = 0.0;

    @Column(name = "ratings_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer ratingsCount = 0;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeRatingEntity> ratings = new ArrayList<>();

    @Column(name = "comments_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer commentsCount = 0;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeCommentEntity> comments = new ArrayList<>();

    @Column(name = "created_at", insertable = false, updatable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;
}
