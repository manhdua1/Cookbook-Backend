package com.dao.cookbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

/**
 * Entity class representing a like on a recipe.
 * <p>
 * Maps to the "recipe_likes" table in the database.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the like (primary key).</li>
 *   <li><b>userId</b>: ID of the user who liked the recipe (foreign key).</li>
 *   <li><b>recipeId</b>: ID of the recipe that was liked (foreign key).</li>
 *   <li><b>user</b>: The user who liked the recipe.</li>
 *   <li><b>recipe</b>: The recipe that was liked.</li>
 *   <li><b>createdAt</b>: Timestamp when the like was created.</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "recipe_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "recipe_id"})
})
public class RecipeLikeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    private RecipeEntity recipe;

    @Column(name = "created_at", insertable = false, updatable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
}
