package com.dao.cookbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for ingredient information.
 */
@Data
public class IngredientDTO {
    
    private Long id;
    
    @NotBlank(message = "Tên nguyên liệu không được để trống")
    private String name;
    
    private String quantity;
    
    private String unit;
}
