package com.dao.cookbook.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RecipeFilterRequestDTO {
    private List<String> includeIngredients; // Danh sách nguyên liệu phải có
    private List<String> excludeIngredients; // Danh sách nguyên liệu không được có
}
