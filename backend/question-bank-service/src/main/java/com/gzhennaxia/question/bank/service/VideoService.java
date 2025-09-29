package com.gzhennaxia.question.bank.service;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class VideoService {

    // FFmpeg工具所在目录（如 D:\SOFTERWARE\ffmpeg-7.1.1-full_build\bin）
    private final String ffmpegDir;

    // 各工具的完整路径（自动从目录推导）
    private final String ffmpegPath;
    private final String ffprobePath;
    private final String ffplayPath;

    /**
     * 构造方法：传入FFmpeg工具所在目录
     *
     * @param ffmpegDir FFmpeg的bin目录（包含ffmpeg.exe、ffprobe.exe等）
     */
    public VideoService(String ffmpegDir) {
        this.ffmpegDir = ffmpegDir;

        // 自动拼接各工具的完整路径
        this.ffmpegPath = getToolPath("ffmpeg.exe");
        this.ffprobePath = getToolPath("ffprobe.exe");
        this.ffplayPath = getToolPath("ffplay.exe"); // 可选，如需播放功能

        // 验证核心工具是否存在
        validateTool(ffmpegPath, "ffmpeg");
        validateTool(ffprobePath, "ffprobe");
    }

    /**
     * 拼接工具的完整路径
     */
    private String getToolPath(String toolName) {
        return ffmpegDir + File.separator + toolName;
    }

    /**
     * 验证工具是否存在且可执行
     */
    private void validateTool(String toolPath, String toolName) {
        File toolFile = new File(toolPath);
        if (!toolFile.exists() || !toolFile.canExecute()) {
            throw new RuntimeException(toolName + "不存在或不可执行: " + toolPath);
        }
    }

    /**
     * 对视频每隔指定时间截图一次
     */
    public List<File> captureScreenshots(String videoPath, String outputDir, int intervalSeconds)
            throws IOException, InterruptedException {

        // videoPath 是 [P1]、[P2]、[P10] 等开头的， 从 videoPath 提取出 “P1”、 “P2”、 “P10”
        String pName = videoPath.substring(videoPath.indexOf("[") + 1, videoPath.indexOf("]"));
        if (StringUtils.isNotEmpty(pName)) {
            outputDir += File.separator + pName;
        }

        // 创建输出目录
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        // 获取视频总时长(秒) - 使用ffprobe
        int duration = getVideoDurationByFFprobe(videoPath);
        if (duration <= 0) {
            throw new RuntimeException("无法获取视频时长或视频时长为0");
        }

        List<File> screenshotFiles = new ArrayList<>();

        // 计算需要截图的时间点
        for (int time = 0; time < duration; time += intervalSeconds) {
            String outputFilePath = outputDir + File.separator + "screenshot_" + time + "s.jpg";
            File screenshotFile = new File(outputFilePath);

            // 执行截图命令（使用ffmpeg）
            boolean success = captureSingleFrame(videoPath, outputFilePath, time);
            if (success) {
                screenshotFiles.add(screenshotFile);
                System.out.println("已生成截图: " + outputFilePath);
            }
        }

        return screenshotFiles;
    }

    /**
     * 使用ffprobe获取视频时长
     */
    private int getVideoDurationByFFprobe(String videoPath) throws IOException, InterruptedException {
        // 验证视频文件
        File videoFile = new File(videoPath);
        if (!videoFile.exists() || !videoFile.canRead()) {
            throw new IOException("视频文件不存在或无法读取: " + videoPath);
        }

        // 构建ffprobe命令（复用验证通过的参数）
        List<String> command = new ArrayList<>();
        command.add(ffprobePath);
        command.add("-i");
        command.add(videoPath);
        command.add("-show_entries");
        command.add("format=duration");
        command.add("-v");
        command.add("quiet");
        command.add("-of");
        command.add("csv=p=0");

        // 打印命令，方便终端验证
        System.out.println("执行FFprobe命令: " + String.join(" ", command));

        // 执行命令
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // 读取输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // 检查执行结果
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFprobe执行失败，错误信息:\n" + output.toString());
        }

        // 解析时长
        String durationStr = output.toString().trim();
        if (durationStr.isEmpty()) {
            throw new RuntimeException("FFprobe未输出时长，输出为空");
        }
        try {
            double duration = Double.parseDouble(durationStr);
            return (int) Math.ceil(duration);
        } catch (NumberFormatException e) {
            throw new RuntimeException("解析时长失败，FFprobe输出: " + durationStr, e);
        }
    }

    /**
     * 使用ffmpeg截取视频指定时间点的一帧
     */
    private boolean captureSingleFrame(String videoPath, String outputPath, int timeSeconds)
            throws IOException, InterruptedException {

        // 构建ffmpeg截图命令
        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.add("-ss"); // 指定开始时间
        command.add(String.valueOf(timeSeconds));
        command.add("-i"); // 输入文件
        command.add(videoPath);
        command.add("-vframes"); // 只截取一帧
        command.add("1");
        command.add("-q:v"); // 画质(1-31, 1最好)
        command.add("2");
        command.add("-y"); // 覆盖已有文件
        command.add(outputPath);

        // 打印命令，方便终端验证
        System.out.println("执行FFmpeg命令: " + String.join(" ", command));

        // 执行命令
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // 读取输出流（防止进程阻塞）
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 可选：打印详细输出
                // System.out.println("FFmpeg输出: " + line);
            }
        }

        // 等待执行完成
        int exitCode = process.waitFor();
        return exitCode == 0;
    }

    // 示例用法
    public static void main(String[] args) {
        try {
            // 传入FFmpeg的bin目录（包含所有工具）
            String ffmpegDir = "D:\\SOFTERWARE\\ffmpeg-7.1.1-full_build\\bin";

            VideoService util = new VideoService(ffmpegDir);

            // 视频路径、输出目录、截图间隔(5秒)
//            List<File> screenshots = util.captureScreenshots(
//                    "D:\\SOFTERWARE\\BBDown_1.6.3_20240814_win-x64\\p1.mp4",
//                    "D:\\SOFTERWARE\\BBDown_1.6.3_20240814_win-x64\\screenshots",
//                    5
//            );
            List<File> screenshots = util.captureScreenshots(
                    "D:\\SOFTERWARE\\BBDown_1.6.3_20240814_win-x64\\章晓铭逻辑22杀（2026全新，逻辑判断，适合公务员考试、事业编、选调、三支一扶；国考、省考、适合0基础和老手）\\[P16]章晓铭逻辑22杀：第16杀-搭桥与拆桥思维.mp4",
                    "D:\\SOFTERWARE\\BBDown_1.6.3_20240814_win-x64\\章晓铭逻辑22杀（2026全新，逻辑判断，适合公务员考试、事业编、选调、三支一扶；国考、省考、适合0基础和老手）\\screenshots",
                    30
            );

            //总结为MD文档
            //1. 保留完整的题干和选项
            //2. 保留完整的知识点
            System.out.println("截图完成，共生成 " + screenshots.size() + " 张图片");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
