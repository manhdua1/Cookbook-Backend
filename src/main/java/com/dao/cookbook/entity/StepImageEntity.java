package com.dao.cookbook.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity class representing an image for a cooking step.
 * <p>
 * Maps to the "step_images" table in the database.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the image (primary key).</li>
 *   <li><b>stepId</b>: ID of the step this image belongs to (foreign key).</li>
 *   <li><b>recipeStep</b>: The recipe step this image belongs to.</li>
 *   <li><b>imageUrl</b>: URL to the image (not null).</li>
 *   <li><b>orderNumber</b>: Order of this image in the step.</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "step_images")
public class StepImageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "step_id", nullable = false)
    private Long stepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", insertable = false, updatable = false)
    private RecipeStepEntity recipeStep;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "order_number")
    private Integer orderNumber;
}
