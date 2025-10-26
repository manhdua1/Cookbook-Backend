package com.dao.cookbook.controller;

import com.dao.cookbook.service.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "File Upload API", description = "API quản lý upload file/ảnh")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Upload ảnh", description = "Upload một file ảnh (jpg, png, gif). File sẽ được lưu vào server và trả về URL")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Upload thành công"),
        @ApiResponse(responseCode = "400", description = "File không hợp lệ hoặc quá lớn"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi lưu file")
    })
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "File ảnh cần upload")
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Thư mục con để lưu file (avatars, recipes, steps)", example = "recipes")
            @RequestParam(value = "type", required = false, defaultValue = "general") String type) {
        
        try {
            // Validate file rỗng
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("File không được để trống"));
            }

            // Validate file là ảnh
            if (!fileStorageService.isImageFile(file)) {
                return ResponseEntity.badRequest().body(createErrorResponse("File phải là ảnh (jpg, png, gif)"));
            }

            // Validate kích thước file (max 5MB)
            if (!fileStorageService.isValidFileSize(file, 5)) {
                return ResponseEntity.badRequest().body(createErrorResponse("File quá lớn. Tối đa 5MB"));
            }

            // Xác định thư mục con dựa trên type
            String subDirectory = getSubDirectory(type);

            // Lưu file
            String fileName = fileStorageService.storeFile(file, subDirectory);

            // Tạo URL để truy cập file
            String fileUrl = baseUrl + "/uploads/" + fileName;

            // Trả về response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Upload ảnh thành công");
            response.put("fileName", fileName);
            response.put("fileUrl", fileUrl);
            response.put("fileSize", file.getSize());
            response.put("contentType", file.getContentType());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Lỗi khi upload file: " + e.getMessage()));
        }
    }

    @Operation(summary = "Upload nhiều ảnh", description = "Upload nhiều file ảnh cùng lúc")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Upload thành công"),
        @ApiResponse(responseCode = "400", description = "Có file không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/images")
    public ResponseEntity<?> uploadMultipleImages(
            @Parameter(description = "Danh sách file ảnh cần upload")
            @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "Thư mục con để lưu file", example = "steps")
            @RequestParam(value = "type", required = false, defaultValue = "general") String type) {
        
        try {
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("Không có file nào được chọn"));
            }

            String subDirectory = getSubDirectory(type);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Upload thành công " + files.length + " ảnh");
            response.put("files", new java.util.ArrayList<>());

            for (MultipartFile file : files) {
                if (!file.isEmpty() && fileStorageService.isImageFile(file) 
                        && fileStorageService.isValidFileSize(file, 5)) {
                    
                    String fileName = fileStorageService.storeFile(file, subDirectory);
                    String fileUrl = baseUrl + "/uploads/" + fileName;

                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("fileName", fileName);
                    fileInfo.put("fileUrl", fileUrl);
                    fileInfo.put("fileSize", file.getSize());
                    
                    ((java.util.List<Object>) response.get("files")).add(fileInfo);
                }
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Lỗi khi upload files: " + e.getMessage()));
        }
    }

    /**
     * Helper method để tạo error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }

    /**
     * Helper method để xác định thư mục con
     */
    private String getSubDirectory(String type) {
        return switch (type.toLowerCase()) {
            case "avatar", "avatars" -> "avatars";
            case "recipe", "recipes" -> "recipes";
            case "step", "steps" -> "steps";
            default -> "general";
        };
    }
}
