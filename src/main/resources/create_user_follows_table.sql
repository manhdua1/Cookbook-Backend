-- Create user_follows table for following functionality
CREATE TABLE IF NOT EXISTS user_follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_user_follows_follower 
        FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_follows_following 
        FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Unique constraint: a user can only follow another user once
    CONSTRAINT uk_user_follows_unique 
        UNIQUE (follower_id, following_id),
    
    -- Prevent self-following (optional check)
    CONSTRAINT chk_user_follows_no_self 
        CHECK (follower_id != following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for better query performance
CREATE INDEX idx_user_follows_follower ON user_follows(follower_id);
CREATE INDEX idx_user_follows_following ON user_follows(following_id);
CREATE INDEX idx_user_follows_created_at ON user_follows(created_at);
