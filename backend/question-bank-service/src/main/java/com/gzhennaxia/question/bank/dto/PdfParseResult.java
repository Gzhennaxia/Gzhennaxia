package com.gzhennaxia.question.bank.dto;

import java.util.List;

/**
 * PDF解析结果DTO
 */
public class PdfParseResult {
    
    private String fileName;
    private String filePath;
    private String text;
    private List<String> imagePaths;
    private int pageCount;
    private long fileSize;
    
    // 构造函数
    public PdfParseResult() {}
    
    public PdfParseResult(String fileName, String text, List<String> imagePaths) {
        this.fileName = fileName;
        this.text = text;
        this.imagePaths = imagePaths;
    }
    
    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }
    
    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }
    
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    
    @Override
    public String toString() {
        return "PdfParseResult{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", pageCount=" + pageCount +
                ", imageCount=" + (imagePaths != null ? imagePaths.size() : 0) +
                ", fileSize=" + fileSize +
                '}';
    }
}