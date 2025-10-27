package com.dao.cookbook.service;

import com.dao.cookbook.dto.request.AdminRecipeRequestDTO;
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
        return getAllRecipes(null);
    }
    
    /**
     * Get all recipes with like info for current user.
     */
    public List<RecipeResponseDTO> getAllRecipes(Long currentUserId) {
        return recipeRepository.findAll().stream()
                .map(recipe -> recipeMapper.toResponse(recipe, currentUserId))
                .collect(Collectors.toList());
    }

    /**
     * Get recipe by ID.
     */
    public RecipeResponseDTO getRecipeById(Long id) {
        return getRecipeById(id, null);
    }
    
    /**
     * Get recipe by ID with like info for current user.
     */
    public RecipeResponseDTO getRecipeById(Long id, Long currentUserId) {
        RecipeEntity recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + id));
        return recipeMapper.toResponse(recipe, currentUserId);
    }

    /**
     * Get all recipes by user ID.
     */
    public List<RecipeResponseDTO> getRecipesByUserId(Long userId) {
        return getRecipesByUserId(userId, null);
    }
    
    /**
     * Get all recipes by user ID with like info for current user.
     */
    public List<RecipeResponseDTO> getRecipesByUserId(Long userId, Long currentUserId) {
        return recipeRepository.findByUserId(userId).stream()
                .map(recipe -> recipeMapper.toResponse(recipe, currentUserId))
                .collect(Collectors.toList());
    }

    /**
     * Search recipes by title.
     */
    public List<RecipeResponseDTO> searchRecipesByTitle(String title) {
        return searchRecipesByTitle(title, null);
    }
    
    /**
     * Search recipes by title with like info for current user.
     */
    public List<RecipeResponseDTO> searchRecipesByTitle(String title, Long currentUserId) {
        return recipeRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(recipe -> recipeMapper.toResponse(recipe, currentUserId))
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

    /**
     * Create a new recipe with AdminRecipeRequestDTO.
     * This method allows admins to create recipes for any user.
     */
    @Transactional
    public RecipeResponseDTO createRecipe(AdminRecipeRequestDTO dto, Long userId) {
        // Create RecipeRequestDTO from AdminRecipeRequestDTO
        RecipeRequestDTO recipeDTO = new RecipeRequestDTO();
        recipeDTO.setTitle(dto.getTitle());
        recipeDTO.setImageUrl(dto.getImageUrl());
        recipeDTO.setServings(dto.getServings());
        recipeDTO.setCookingTime(dto.getCookingTime());
        recipeDTO.setIngredients(dto.getIngredients());
        recipeDTO.setSteps(dto.getSteps());
        
        // Use the existing createRecipe method
        return createRecipe(recipeDTO, userId);
    }

    /**
     * Update recipe by admin (no ownership check).
     */
    @Transactional
    public RecipeResponseDTO updateRecipeByAdmin(Long id, AdminRecipeRequestDTO dto) {
        // Find existing recipe
        RecipeEntity recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + id));

        // Create RecipeRequestDTO from AdminRecipeRequestDTO
        RecipeRequestDTO recipeDTO = new RecipeRequestDTO();
        recipeDTO.setTitle(dto.getTitle());
        recipeDTO.setImageUrl(dto.getImageUrl());
        recipeDTO.setServings(dto.getServings());
        recipeDTO.setCookingTime(dto.getCookingTime());
        recipeDTO.setIngredients(dto.getIngredients());
        recipeDTO.setSteps(dto.getSteps());

        // Update basic recipe info
        recipeMapper.updateEntity(recipe, recipeDTO);
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
     * Delete recipe by admin (no ownership check).
     */
    @Transactional
    public void deleteRecipeByAdmin(Long id) {
        RecipeEntity recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + id));

        recipeRepository.delete(recipe);
    }
}
