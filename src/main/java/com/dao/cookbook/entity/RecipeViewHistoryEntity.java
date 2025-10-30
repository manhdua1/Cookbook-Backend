package com.dao.cookbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

/**
 * Entity class representing a user's recipe view history.
 * <p>
 * Maps to the "recipe_view_history" table in the database.
 * Tracks when users view recipe details.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the view history entry (primary key).</li>
 *   <li><b>userId</b>: ID of the user who viewed the recipe (foreign key).</li>
 *   <li><b>recipeId</b>: ID of the recipe that was viewed (foreign key).</li>
 *   <li><b>viewedAt</b>: Timestamp when the recipe was viewed.</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "recipe_view_history")
public class RecipeViewHistoryEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    private RecipeEntity recipe;

    @Column(name = "viewed_at", insertable = false, updatable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp viewedAt;
}
