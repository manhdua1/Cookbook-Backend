package com.dao.cookbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating or updating a comment.
 */
@Data
public class CommentRequestDTO {
    
    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(min = 1, max = 1000, message = "Bình luận phải từ 1 đến 1000 ký tự")
    private String comment;
    
    private Long parentCommentId; // Optional: for replies
}
