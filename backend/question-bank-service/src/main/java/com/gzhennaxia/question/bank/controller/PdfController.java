package com.gzhennaxia.question.bank.controller;

import com.gzhennaxia.question.bank.service.PdfParserService;
import com.gzhennaxia.question.bank.dto.PdfParseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * PDF处理控制器
 */
@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
public class PdfController {
    
    @Autowired
    private PdfParserService pdfParserService;
    
    /**
     * 上传并解析PDF文件
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件类型
            if (!file.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest().body("只支持PDF文件格式");
            }
            
            // 验证文件大小（限制为50MB）
            if (file.getSize() > 50 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("文件大小不能超过50MB");
            }
            
            PdfParseResult result = pdfParserService.parsePdf(file);
            return ResponseEntity.ok(result);
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("PDF解析失败: " + e.getMessage());
        }
    }
    
    /**
     * 按页解析PDF
     */
    @PostMapping("/parse-by-pages")
    public ResponseEntity<?> parseByPages(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest().body("只支持PDF文件格式");
            }
            
            List<String> pageTexts = pdfParserService.parseByPages(file);
            return ResponseEntity.ok(pageTexts);
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("PDF解析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取支持的文件格式信息
     */
    @GetMapping("/supported-formats")
    public ResponseEntity<?> getSupportedFormats() {
        return ResponseEntity.ok(new String[]{"application/pdf"});
    }
}