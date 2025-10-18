-- Database migration script for Recipe Comments and Ratings feature
-- This script creates the recipe_comments and recipe_ratings tables
-- and adds rating/comment related columns to recipes table

-- Create recipe_comments table
CREATE TABLE IF NOT EXISTS recipe_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    comment TEXT NOT NULL,
    parent_comment_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES recipe_comments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create recipe_ratings table
CREATE TABLE IF NOT EXISTS recipe_ratings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_recipe_rating (user_id, recipe_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add rating and comment related columns to recipes table
ALTER TABLE recipes 
ADD COLUMN IF NOT EXISTS average_rating DECIMAL(3,2) DEFAULT 0.00,
ADD COLUMN IF NOT EXISTS ratings_count INT NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS comments_count INT NOT NULL DEFAULT 0;

-- Create indexes for better query performance
CREATE INDEX idx_recipe_comments_recipe_id ON recipe_comments(recipe_id);
CREATE INDEX idx_recipe_comments_user_id ON recipe_comments(user_id);
CREATE INDEX idx_recipe_comments_parent_id ON recipe_comments(parent_comment_id);
CREATE INDEX idx_recipe_comments_created_at ON recipe_comments(created_at);

CREATE INDEX idx_recipe_ratings_recipe_id ON recipe_ratings(recipe_id);
CREATE INDEX idx_recipe_ratings_user_id ON recipe_ratings(user_id);
CREATE INDEX idx_recipe_ratings_rating ON recipe_ratings(rating);
CREATE INDEX idx_recipe_ratings_created_at ON recipe_ratings(created_at);

-- Update existing recipes to have correct ratings statistics
-- This calculates average rating and ratings count for existing data
UPDATE recipes r
SET 
    average_rating = (
        SELECT ROUND(AVG(rr.rating), 2)
        FROM recipe_ratings rr
        WHERE rr.recipe_id = r.id
    ),
    ratings_count = (
        SELECT COUNT(*)
        FROM recipe_ratings rr
        WHERE rr.recipe_id = r.id
    ),
    comments_count = (
        SELECT COUNT(*)
        FROM recipe_comments rc
        WHERE rc.recipe_id = r.id AND rc.parent_comment_id IS NULL
    )
WHERE EXISTS (
    SELECT 1 FROM recipe_ratings rr WHERE rr.recipe_id = r.id
) OR EXISTS (
    SELECT 1 FROM recipe_comments rc WHERE rc.recipe_id = r.id
);

-- Verification query (optional - comment out if not needed)
-- SELECT 
--     r.id,
--     r.title,
--     r.average_rating,
--     r.ratings_count,
--     r.comments_count,
--     ROUND(AVG(rr.rating), 2) as calculated_avg,
--     COUNT(DISTINCT rr.id) as actual_ratings_count,
--     COUNT(DISTINCT rc.id) as actual_comments_count
-- FROM recipes r
-- LEFT JOIN recipe_ratings rr ON r.id = rr.recipe_id
-- LEFT JOIN recipe_comments rc ON r.id = rc.recipe_id AND rc.parent_comment_id IS NULL
-- GROUP BY r.id, r.title, r.average_rating, r.ratings_count, r.comments_count;
