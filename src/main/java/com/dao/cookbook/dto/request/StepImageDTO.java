package com.dao.cookbook.dto.request;

import lombok.Data;

/**
 * DTO for step image information.
 */
@Data
public class StepImageDTO {
    
    private Long id;
    
    private String imageUrl;
    
    private Integer orderNumber;
}
