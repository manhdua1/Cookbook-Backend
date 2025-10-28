package com.dao.cookbook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file.", ex);
        }
    }

    /**
     * Lưu file và trả về tên file đã lưu
     */
    public String storeFile(MultipartFile file, String subDirectory) {
        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File rỗng");
        }

        // Lấy tên file gốc
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Kiểm tra tên file có ký tự không hợp lệ
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Tên file không hợp lệ: " + originalFileName);
            }

            // Tạo tên file unique bằng UUID
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Tạo subdirectory nếu cần
            Path targetLocation = this.fileStorageLocation;
            if (subDirectory != null && !subDirectory.isEmpty()) {
                targetLocation = this.fileStorageLocation.resolve(subDirectory);
                Files.createDirectories(targetLocation);
            }

            // Copy file vào thư mục đích
            Path destinationFile = targetLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn relative
            if (subDirectory != null && !subDirectory.isEmpty()) {
                return subDirectory + "/" + newFileName;
            }
            return newFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file: " + originalFileName, ex);
        }
    }

    /**
     * Xóa file
     */
    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Không thể xóa file: " + fileName, ex);
        }
    }

    /**
     * Validate file là ảnh (kiểm tra cả content-type và extension)
     */
    public boolean isImageFile(MultipartFile file) {
        // Kiểm tra content-type
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        
        // Nếu content-type không có hoặc không phải image, kiểm tra extension
        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null) {
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
            return extension.equals("jpg") || extension.equals("jpeg") || 
                   extension.equals("png") || extension.equals("gif") || 
                   extension.equals("webp") || extension.equals("bmp");
        }
        
        return false;
    }

    /**
     * Validate kích thước file
     */
    public boolean isValidFileSize(MultipartFile file, long maxSizeInMB) {
        long maxSizeInBytes = maxSizeInMB * 1024 * 1024;
        return file.getSize() <= maxSizeInBytes;
    }
}
