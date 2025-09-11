package com.questionbank.service;

import com.questionbank.dto.PdfParseResult;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PDF解析服务
 */
@Service
public class PdfParserService {
    
    @Autowired
    private ImageStorageService imageStorageService;
    
    private static final String UPLOAD_DIR = "uploads/pdfs/";
    private static final String IMAGE_DIR = "uploads/images/";
    
    /**
     * 解析PDF文件
     */
    public PdfParseResult parsePdf(MultipartFile file) throws IOException {
        // 创建上传目录
        createDirectories();
        
        // 保存PDF文件
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path pdfPath = Paths.get(UPLOAD_DIR + fileName);
        Files.copy(file.getInputStream(), pdfPath);
        
        PdfParseResult result = new PdfParseResult();
        result.setFileName(file.getOriginalFilename());
        result.setFilePath(pdfPath.toString());
        
        try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {
            // 提取文本
            String text = extractText(document);
            result.setText(text);
            
            // 提取图片
            List<String> imagePaths = extractImages(document, fileName);
            result.setImagePaths(imagePaths);
            
            // 设置页数
            result.setPageCount(document.getNumberOfPages());
            
        } catch (Exception e) {
            throw new IOException("PDF解析失败: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    /**
     * 提取PDF中的文本
     */
    private String extractText(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }
    
    /**
     * 提取PDF中的图片
     */
    private List<String> extractImages(PDDocument document, String pdfFileName) throws IOException {
        List<String> imagePaths = new ArrayList<>();
        int imageIndex = 0;
        
        for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
            PDPage page = document.getPage(pageIndex);
            PDResources resources = page.getResources();
            
            if (resources != null) {
                for (String name : resources.getXObjectNames()) {
                    PDXObject xObject = resources.getXObject(name);
                    
                    if (xObject instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject) xObject;
                        
                        // 生成图片文件名
                        String imageFileName = String.format("%s_page_%d_img_%d.%s", 
                            pdfFileName.replaceAll("\\.[^.]*$", ""), 
                            pageIndex + 1, 
                            imageIndex++, 
                            getImageFormat(image));
                        
                        // 保存图片
                        String imagePath = saveImage(image, imageFileName);
                        if (imagePath != null) {
                            imagePaths.add(imagePath);
                        }
                    } else if (xObject instanceof PDFormXObject) {
                        // 处理表单对象中的图片
                        extractImagesFromForm((PDFormXObject) xObject, imagePaths, pdfFileName, pageIndex, imageIndex);
                    }
                }
            }
        }
        
        return imagePaths;
    }
    
    /**
     * 从表单对象中提取图片
     */
    private void extractImagesFromForm(PDFormXObject form, List<String> imagePaths, 
                                     String pdfFileName, int pageIndex, int imageIndex) throws IOException {
        PDResources resources = form.getResources();
        if (resources != null) {
            for (String name : resources.getXObjectNames()) {
                PDXObject xObject = resources.getXObject(name);
                if (xObject instanceof PDImageXObject) {
                    PDImageXObject image = (PDImageXObject) xObject;
                    
                    String imageFileName = String.format("%s_page_%d_form_img_%d.%s", 
                        pdfFileName.replaceAll("\\.[^.]*$", ""), 
                        pageIndex + 1, 
                        imageIndex++, 
                        getImageFormat(image));
                    
                    String imagePath = saveImage(image, imageFileName);
                    if (imagePath != null) {
                        imagePaths.add(imagePath);
                    }
                }
            }
        }
    }
    
    /**
     * 保存图片到文件系统
     */
    private String saveImage(PDImageXObject image, String fileName) {
        try {
            BufferedImage bufferedImage = image.getImage();
            if (bufferedImage != null) {
                Path imagePath = Paths.get(IMAGE_DIR + fileName);
                File imageFile = imagePath.toFile();
                
                String format = getImageFormat(image);
                ImageIO.write(bufferedImage, format, imageFile);
                
                return imagePath.toString();
            }
        } catch (IOException e) {
            System.err.println("保存图片失败: " + fileName + ", 错误: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 获取图片格式
     */
    private String getImageFormat(PDImageXObject image) {
        String suffix = image.getSuffix();
        if (suffix != null) {
            return suffix.toLowerCase();
        }
        return "png"; // 默认格式
    }
    
    /**
     * 创建必要的目录
     */
    private void createDirectories() throws IOException {
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        Files.createDirectories(Paths.get(IMAGE_DIR));
    }
    
    /**
     * 按页解析PDF
     */
    public List<String> parseByPages(MultipartFile file) throws IOException {
        List<String> pageTexts = new ArrayList<>();
        
        // 保存PDF文件
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path pdfPath = Paths.get(UPLOAD_DIR + fileName);
        Files.copy(file.getInputStream(), pdfPath);
        
        try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            
            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String pageText = stripper.getText(document);
                pageTexts.add(pageText);
            }
        }
        
        return pageTexts;
    }
}