package com.dao.cookbook.controller;

import com.dao.cookbook.entity.SearchHistoryEntity;
import com.dao.cookbook.service.SearchHistoryService;
import com.dao.cookbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search-history")
@Tag(name = "Search History API", description = "API quản lý lịch sử tìm kiếm của người dùng")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;
    private final UserService userService;

    public SearchHistoryController(SearchHistoryService searchHistoryService, UserService userService) {
        this.searchHistoryService = searchHistoryService;
        this.userService = userService;
    }

    /**
     * Get current authenticated user ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }
        
        String email = authentication.getName();
        return userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"))
                .getId();
    }

    @Operation(
        summary = "Lấy lịch sử tìm kiếm", 
        description = "Lấy danh sách lịch sử tìm kiếm của người dùng hiện tại. Mặc định trả về các query duy nhất (không trùng lặp)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy lịch sử thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping
    public ResponseEntity<?> getSearchHistory(
            @Parameter(description = "Số lượng kết quả tối đa (mặc định: 20)", example = "20")
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @Parameter(description = "Trả về tất cả lịch sử (bao gồm trùng lặp) hay chỉ query duy nhất", example = "false")
            @RequestParam(required = false, defaultValue = "false") Boolean showAll) {
        try {
            Long userId = getCurrentUserId();
            
            if (showAll) {
                // Trả về tất cả lịch sử
                List<SearchHistoryEntity> history = searchHistoryService.getUserSearchHistory(userId);
                List<SearchHistoryEntity> limitedHistory = history.stream()
                        .limit(limit)
                        .toList();
                
                Map<String, Object> response = new HashMap<>();
                response.put("total", history.size());
                response.put("showing", limitedHistory.size());
                response.put("history", limitedHistory);
                
                return ResponseEntity.ok(response);
            } else {
                // Trả về query duy nhất (mặc định)
                List<String> uniqueQueries = searchHistoryService.getRecentUniqueSearchQueries(userId, limit);
                
                Map<String, Object> response = new HashMap<>();
                response.put("total", uniqueQueries.size());
                response.put("queries", uniqueQueries);
                
                return ResponseEntity.ok(response);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Lưu lịch sử tìm kiếm", 
        description = "Lưu một query tìm kiếm vào lịch sử của người dùng hiện tại"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Lưu lịch sử thành công"),
        @ApiResponse(responseCode = "400", description = "Query không hợp lệ"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @PostMapping
    public ResponseEntity<?> saveSearchHistory(
            @Parameter(description = "Query tìm kiếm", example = "phở bò")
            @RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Query không được để trống");
            }
            
            Long userId = getCurrentUserId();
            SearchHistoryEntity saved = searchHistoryService.saveSearchHistory(userId, query);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Xóa toàn bộ lịch sử tìm kiếm", 
        description = "Xóa tất cả lịch sử tìm kiếm của người dùng hiện tại"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @DeleteMapping
    public ResponseEntity<String> clearSearchHistory() {
        try {
            Long userId = getCurrentUserId();
            searchHistoryService.clearUserSearchHistory(userId);
            
            return ResponseEntity.ok("Đã xóa toàn bộ lịch sử tìm kiếm");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Xóa một query cụ thể", 
        description = "Xóa một query tìm kiếm cụ thể khỏi lịch sử của người dùng hiện tại"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @ApiResponse(responseCode = "400", description = "Query không hợp lệ"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @DeleteMapping("/query")
    public ResponseEntity<String> deleteSearchQuery(
            @Parameter(description = "Query cần xóa", example = "phở bò")
            @RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Query không được để trống");
            }
            
            Long userId = getCurrentUserId();
            searchHistoryService.deleteSearchQuery(userId, query);
            
            return ResponseEntity.ok("Đã xóa query: " + query);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Thống kê lịch sử tìm kiếm", 
        description = "Lấy thông tin thống kê về lịch sử tìm kiếm của người dùng"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy thống kê thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping("/stats")
    public ResponseEntity<?> getSearchStats() {
        try {
            Long userId = getCurrentUserId();
            
            long totalSearches = searchHistoryService.getUserSearchCount(userId);
            List<String> uniqueQueries = searchHistoryService.getUniqueSearchQueries(userId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSearches", totalSearches);
            stats.put("uniqueQueries", uniqueQueries.size());
            stats.put("recentQueries", uniqueQueries.stream().limit(5).toList());
            
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
