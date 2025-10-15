package com.dao.cookbook.repository;

import com.dao.cookbook.entity.RecipeStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for RecipeStepEntity operations.
 */
@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStepEntity, Long> {
}
