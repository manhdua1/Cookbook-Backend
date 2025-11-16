package com.dao.cookbook.repository;

import com.dao.cookbook.entity.StepImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for StepImageEntity operations.
 */
@Repository
public interface StepImageRepository extends JpaRepository<StepImageEntity, Long> {
}
