package com.dao.cookbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

/**
 * Entity class representing a user's search history.
 * <p>
 * Maps to the "search_history" table in the database.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the search history entry (primary key).</li>
 *   <li><b>userId</b>: ID of the user who performed the search (foreign key).</li>
 *   <li><b>searchQuery</b>: The search query text entered by the user.</li>
 *   <li><b>searchedAt</b>: Timestamp when the search was performed.</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "search_history")
public class SearchHistoryEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "search_query", nullable = false, length = 255)
    private String searchQuery;

    @Column(name = "searched_at", insertable = false, updatable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp searchedAt;
}
