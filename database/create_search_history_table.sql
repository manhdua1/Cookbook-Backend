-- Tạo bảng search_history để lưu lịch sử tìm kiếm của người dùng
CREATE TABLE IF NOT EXISTS search_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    search_query VARCHAR(255) NOT NULL,
    searched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    CONSTRAINT fk_search_history_user
        FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE,
    
    -- Index để tìm kiếm nhanh theo user_id
    INDEX idx_user_id (user_id),
    
    -- Index để tìm kiếm theo thời gian
    INDEX idx_searched_at (searched_at),
    
    -- Index composite để optimize query lấy lịch sử của user theo thời gian
    INDEX idx_user_searched (user_id, searched_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
