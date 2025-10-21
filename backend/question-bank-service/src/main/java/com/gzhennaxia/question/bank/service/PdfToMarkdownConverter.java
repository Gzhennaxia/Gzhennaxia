package com.gzhennaxia.question.bank.service;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author: li.bo
 * @date: 2025/10/21 23:08
 * @version: v1.0
 */
public class PdfToMarkdownConverter {

    // 图片保存目录（相对于输出的Markdown文件）
    private static final String IMAGE_DIR = "images/";

    /**
     * 将PDF指定页转换为Markdown
     *
     * @param pdfPath      PDF文件路径
     * @param startPage    起始页码（1-based）
     * @param endPage      结束页码（1-based）
     * @param mdOutputPath 输出Markdown文件路径
     */
    public static void convert(String pdfPath, int startPage, int endPage, String mdOutputPath) throws Exception {
        // 1. 提取指定页文字
        String pageText = extractPageText(pdfPath, startPage, endPage);
        // 2. 提取指定页图片并替换文字中的图片占位符
        String textWithImageMarkers = extractAndMarkImages(pdfPath, startPage, endPage, pageText, mdOutputPath);
        // 3. 结构化处理文字为Markdown格式
        String markdownContent = structureToMarkdown(textWithImageMarkers);
        // 4. 写入Markdown文件
        Files.write(Paths.get(mdOutputPath), markdownContent.getBytes());
        System.out.println("转换完成，Markdown文件路径：" + mdOutputPath);
    }

    /**
     * 提取PDF指定页的文字
     */
    private static String extractPageText(String pdfPath, int startPage, int endPage) throws Exception {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            return stripper.getText(document);
        }
    }

    /**
     * 提取指定页图片并在文字中标记位置
     */
    private static String extractAndMarkImages(String pdfPath, int startPage, int endPage,
                                               String originalText, String mdOutputPath) throws Exception {
        // 创建图片保存目录（与Markdown同目录下的images文件夹）
        String imageSaveDir = new File(mdOutputPath).getParent() + File.separator + IMAGE_DIR;
        Files.createDirectories(Paths.get(imageSaveDir));

        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            int imageIndex = 1;
            // 遍历指定页提取图片
            for (int pageNum = startPage; pageNum <= endPage; pageNum++) {
                PDPage page = document.getPage(pageNum - 1); // PDFBox页码0-based
                // 提取页面中的图片（简化版：实际需遍历页面内容流识别图片）

                PDResources resources = page.getResources();
                for (COSName name : resources.getXObjectNames()) {
                    PDXObject xobject = resources.getXObject(name);
                    if (xobject instanceof PDImageXObject) {
                        // 处理图片对象
                        PDImageXObject image = (PDImageXObject) xobject;
                        // 可以通过image获取图片数据等信息
                        // 保存图片到本地
                        String imageFileName = "page_" + pageNum + "_img_" + imageIndex + "." + image.getSuffix();
                        String imagePath = imageSaveDir + imageFileName;
                        BufferedImage bufferedImage = image.getImage(); // 获取 BufferedImage
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", baos); // 转为指定格式的字节流
                        byte[] imageBytes = baos.toByteArray(); // 得到字节数组

                        Files.write(Paths.get(imagePath), imageBytes);
                        // 在文字中插入图片占位符（后续替换为Markdown图片语法）
                        originalText = originalText.replaceFirst("\f",
                                "![图片" + imageIndex + "](" + IMAGE_DIR + imageFileName + ")\n\n");
                        imageIndex++;
                    }
                }

//                for (PDImageXObject image : page.getResources().getImages().values()) {
//                    // 保存图片到本地
//                    String imageFileName = "page_" + pageNum + "_img_" + imageIndex + "." + image.getSuffix();
//                    String imagePath = imageSaveDir + imageFileName;
//                    Files.write(Paths.get(imagePath), image.getImageAsBytes());
//                    // 在文字中插入图片占位符（后续替换为Markdown图片语法）
//                    originalText = originalText.replaceFirst("\f",
//                            "![图片" + imageIndex + "](" + IMAGE_DIR + imageFileName + ")\n\n");
//                    imageIndex++;
//                }
            }
            return originalText;
        }
    }

    /**
     * 将文字结构化处理为Markdown（基础版：识别标题、段落、列表）
     */
    private static String structureToMarkdown(String text) {
        // 按空行分割段落
        String[] paragraphs = text.split("\n\\s*\n");
        StringBuilder mdBuilder = new StringBuilder();

        // 正则匹配可能的标题（假设标题行以大写字母开头且长度较短）
        Pattern titlePattern = Pattern.compile("^[A-Z0-9][^.]*$");
        // 正则匹配列表项（以数字/符号开头）
        Pattern listPattern = Pattern.compile("^[\\*\\-\\d\\.]\\s+");

        for (String para : paragraphs) {
            para = para.replaceAll("\n", " ").trim(); // 合并段落内换行
            if (para.isEmpty()) continue;

            Matcher titleMatcher = titlePattern.matcher(para);
            Matcher listMatcher = listPattern.matcher(para);

            if (titleMatcher.matches() && para.length() < 50) {
                // 识别为标题（简单处理：假设短标题为H2）
                mdBuilder.append("## ").append(para).append("\n\n");
            } else if (listMatcher.matches()) {
                // 识别为列表项
                mdBuilder.append("- ").append(para.replaceFirst("[\\*\\-\\d\\.]\\s+", "")).append("\n");
            } else {
                // 普通段落
                mdBuilder.append(para).append("\n\n");
            }
        }

        return mdBuilder.toString();
    }

    // 测试示例
    public static void main(String[] args) {
        try {
            String pdfPath = "C:\\FIRE\\CODE\\Gzhennaxia\\docs\\civil-servant\\02-判断推理\\章晓铭-判断推理系统理论课程\\【B站】章晓铭·判断推理理论课讲义.pdf"; // 输入PDF路径
            String mdPath = "C:\\FIRE\\CODE\\Gzhennaxia\\docs\\civil-servant\\02-判断推理\\章晓铭-判断推理系统理论课程\\【B站】章晓铭·判断推理理论课讲义.md";  // 输出Markdown路径
            convert(pdfPath, 52, 53, mdPath); // 转换第1-2页
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}