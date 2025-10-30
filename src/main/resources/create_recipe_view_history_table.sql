-- Create recipe_view_history table for tracking recently viewed recipes
CREATE TABLE IF NOT EXISTS recipe_view_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_recipe_view_history_user 
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_recipe_view_history_recipe 
        FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for better query performance
CREATE INDEX idx_recipe_view_history_user ON recipe_view_history(user_id);
CREATE INDEX idx_recipe_view_history_recipe ON recipe_view_history(recipe_id);
CREATE INDEX idx_recipe_view_history_viewed_at ON recipe_view_history(viewed_at);
CREATE INDEX idx_recipe_view_history_user_recipe ON recipe_view_history(user_id, recipe_id);
CREATE INDEX idx_recipe_view_history_user_viewed ON recipe_view_history(user_id, viewed_at DESC);
