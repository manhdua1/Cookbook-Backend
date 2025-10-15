package com.dao.cookbook.service;

import com.dao.cookbook.dto.request.RecipeRequestDTO;
import com.dao.cookbook.dto.response.RecipeResponseDTO;
import com.dao.cookbook.entity.*;
import com.dao.cookbook.mapper.RecipeMapper;
import com.dao.cookbook.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Recipe business logic.
 */
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final StepImageRepository stepImageRepository;
    private final RecipeMapper recipeMapper;

    public RecipeService(RecipeRepository recipeRepository,
                        IngredientRepository ingredientRepository,
                        RecipeStepRepository recipeStepRepository,
                        StepImageRepository stepImageRepository,
                        RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeStepRepository = recipeStepRepository;
        this.stepImageRepository = stepImageRepository;
        this.recipeMapper = recipeMapper;
    }

    /**
     * Get all recipes.
     */
    public List<RecipeResponseDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get recipe by ID.
     */
    public RecipeResponseDTO getRecipeById(Long id) {
        RecipeEntity recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + id));
        return recipeMapper.toResponse(recipe);
    }

    /**
     * Get all recipes by user ID.
     */
    public List<RecipeResponseDTO> getRecipesByUserId(Long userId) {
        return recipeRepository.findByUserId(userId).stream()
                .map(recipeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search recipes by title.
     */
    public List<RecipeResponseDTO> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(recipeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a new recipe.
     */
    @Transactional
    public RecipeResponseDTO createRecipe(RecipeRequestDTO dto, Long userId) {
        // Create recipe entity
        RecipeEntity recipe = recipeMapper.toEntity(dto, userId);
        RecipeEntity savedRecipe = recipeRepository.save(recipe);

        // Save ingredients
        if (dto.getIngredients() != null && !dto.getIngredients().isEmpty()) {
            List<IngredientEntity> ingredients = dto.getIngredients().stream()
                    .map(ingredientDTO -> recipeMapper.ingredientToEntity(ingredientDTO, savedRecipe.getId()))
                    .collect(Collectors.toList());
            ingredientRepository.saveAll(ingredients);
        }

        // Save steps and their images
        if (dto.getSteps() != null && !dto.getSteps().isEmpty()) {
            for (var stepDTO : dto.getSteps()) {
                RecipeStepEntity step = recipeMapper.stepToEntity(stepDTO, savedRecipe.getId());
                RecipeStepEntity savedStep = recipeStepRepository.save(step);

                // Save step images
                if (stepDTO.getImages() != null && !stepDTO.getImages().isEmpty()) {
                    List<StepImageEntity> images = stepDTO.getImages().stream()
                            .map(imageDTO -> recipeMapper.imageToEntity(imageDTO, savedStep.getId()))
                            .collect(Collectors.toList());
                    stepImageRepository.saveAll(images);
                }
            }
        }

        // Reload recipe with all relationships
        return getRecipeById(savedRecipe.getId());
    }

    /**
     * Update an existing recipe.
     */
    @Transactional
    public RecipeResponseDTO updateRecipe(Long id, RecipeRequestDTO dto, Long userId) {
        // Find existing recipe
        RecipeEntity recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + id));

        // Check if user owns this recipe
        if (!recipe.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa công thức này");
        }

        // Update basic recipe info
        recipeMapper.updateEntity(recipe, dto);
        recipeRepository.save(recipe);

        // Delete old ingredients and create new ones
        if (recipe.getIngredients() != null) {
            ingredientRepository.deleteAll(recipe.getIngredients());
        }
        if (dto.getIngredients() != null && !dto.getIngredients().isEmpty()) {
            List<IngredientEntity> ingredients = dto.getIngredients().stream()
                    .map(ingredientDTO -> recipeMapper.ingredientToEntity(ingredientDTO, recipe.getId()))
                    .collect(Collectors.toList());
            ingredientRepository.saveAll(ingredients);
        }

        // Delete old steps (cascade will delete images)
        if (recipe.getSteps() != null) {
            recipeStepRepository.deleteAll(recipe.getSteps());
        }
        
        // Create new steps and images
        if (dto.getSteps() != null && !dto.getSteps().isEmpty()) {
            for (var stepDTO : dto.getSteps()) {
                RecipeStepEntity step = recipeMapper.stepToEntity(stepDTO, recipe.getId());
                RecipeStepEntity savedStep = recipeStepRepository.save(step);

                if (stepDTO.getImages() != null && !stepDTO.getImages().isEmpty()) {
                    List<StepImageEntity> images = stepDTO.getImages().stream()
                            .map(imageDTO -> recipeMapper.imageToEntity(imageDTO, savedStep.getId()))
                            .collect(Collectors.toList());
                    stepImageRepository.saveAll(images);
                }
            }
        }

        // Reload recipe with all relationships
        return getRecipeById(recipe.getId());
    }

    /**
     * Delete a recipe.
     */
    @Transactional
    public void deleteRecipe(Long id, Long userId) {
        RecipeEntity recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + id));

        // Check if user owns this recipe
        if (!recipe.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa công thức này");
        }

        recipeRepository.delete(recipe);
    }
}
