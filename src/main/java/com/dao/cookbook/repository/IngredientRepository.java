package com.dao.cookbook.repository;

import com.dao.cookbook.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for IngredientEntity operations.
 */
@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {
}
