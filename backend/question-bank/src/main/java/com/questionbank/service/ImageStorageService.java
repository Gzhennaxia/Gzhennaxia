package com.questionbank.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 图片存储服务
 */
@Service
public class ImageStorageService {
    
    private static final String IMAGE_STORAGE_DIR = "uploads/images/";
    
    /**
     * 存储图片文件
     */
    public String storeImage(MultipartFile file) throws IOException {
        // 创建存储目录
        Path storageDir = Paths.get(IMAGE_STORAGE_DIR);
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
        
        // 生成唯一文件名
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
        
        // 保存文件
        Path targetPath = storageDir.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        return targetPath.toString();
    }
    
    /**
     * 存储图片字节数组
     */
    public String storeImage(byte[] imageBytes, String fileName) throws IOException {
        Path storageDir = Paths.get(IMAGE_STORAGE_DIR);
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
        
        Path targetPath = storageDir.resolve(fileName);
        Files.write(targetPath, imageBytes);
        
        return targetPath.toString();
    }
    
    /**
     * 删除图片文件
     */
    public boolean deleteImage(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("删除图片失败: " + imagePath + ", 错误: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查图片是否存在
     */
    public boolean imageExists(String imagePath) {
        return Files.exists(Paths.get(imagePath));
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "jpg"; // 默认扩展名
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
    
    /**
     * 获取图片的完整URL路径
     */
    public String getImageUrl(String imagePath) {
        // 这里可以根据实际部署情况返回完整的URL
        return "/api/images/" + Paths.get(imagePath).getFileName().toString();
    }
}