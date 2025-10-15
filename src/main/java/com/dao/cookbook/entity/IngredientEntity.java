package com.dao.cookbook.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity class representing an ingredient in a recipe.
 * <p>
 * Maps to the "ingredients" table in the database.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the ingredient (primary key).</li>
 *   <li><b>recipeId</b>: ID of the recipe this ingredient belongs to (foreign key).</li>
 *   <li><b>recipe</b>: The recipe this ingredient belongs to.</li>
 *   <li><b>name</b>: Name of the ingredient (not null).</li>
 *   <li><b>quantity</b>: Quantity of the ingredient.</li>
 *   <li><b>unit</b>: Unit of measurement (e.g., kg, g, ml, cups, etc.).</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "ingredients")
public class IngredientEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    private RecipeEntity recipe;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 50)
    private String quantity;

    @Column(length = 50)
    private String unit;
}
