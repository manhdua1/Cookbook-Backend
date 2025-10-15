package com.dao.cookbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for recipe step information.
 */
@Data
public class RecipeStepDTO {
    
    private Long id;
    
    @NotNull(message = "Số thứ tự bước không được để trống")
    private Integer stepNumber;
    
    @NotBlank(message = "Tiêu đề bước không được để trống")
    private String title;
    
    private String description;
    
    private List<StepImageDTO> images = new ArrayList<>();
}
