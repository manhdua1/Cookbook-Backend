package com.dao.cookbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a cooking step in a recipe.
 * <p>
 * Maps to the "recipe_steps" table in the database.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the step (primary key).</li>
 *   <li><b>recipeId</b>: ID of the recipe this step belongs to (foreign key).</li>
 *   <li><b>recipe</b>: The recipe this step belongs to.</li>
 *   <li><b>stepNumber</b>: Order/number of this step in the recipe (not null).</li>
 *   <li><b>title</b>: Title/name of this step (not null).</li>
 *   <li><b>description</b>: Detailed description of this step.</li>
 *   <li><b>images</b>: List of images associated with this step.</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "recipe_steps")
public class RecipeStepEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    private RecipeEntity recipe;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "recipeStep", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StepImageEntity> images = new ArrayList<>();
}
