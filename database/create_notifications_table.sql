-- Tạo bảng notifications để lưu thông báo cho người dùng
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'ID của user nhận thông báo',
    type VARCHAR(50) NOT NULL COMMENT 'Loại thông báo: LIKE, COMMENT, RATING, REPLY, BOOKMARK',
    actor_id BIGINT NOT NULL COMMENT 'ID của user thực hiện hành động',
    recipe_id BIGINT NULL COMMENT 'ID của công thức liên quan',
    comment_id BIGINT NULL COMMENT 'ID của comment liên quan',
    message VARCHAR(500) NOT NULL COMMENT 'Nội dung thông báo',
    is_read BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Đã đọc chưa',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_notification_actor
        FOREIGN KEY (actor_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_notification_recipe
        FOREIGN KEY (recipe_id) 
        REFERENCES recipes(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_notification_comment
        FOREIGN KEY (comment_id) 
        REFERENCES recipe_comments(id) 
        ON DELETE CASCADE,
    
    -- Indexes để optimize query
    INDEX idx_user_id (user_id),
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created_at (created_at),
    INDEX idx_type (type),
    INDEX idx_user_created (user_id, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
