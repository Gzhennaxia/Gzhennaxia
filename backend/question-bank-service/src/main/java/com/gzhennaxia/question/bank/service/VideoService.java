package com.gzhennaxia.question.bank.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class VideoService {

    // FFmpeg可执行文件路径
    private String ffmpegPath;

    public VideoService(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
        // 验证FFmpeg是否存在
        File ffmpegFile = new File(ffmpegPath);
        if (!ffmpegFile.exists() || !ffmpegFile.canExecute()) {
            throw new RuntimeException("FFmpeg不存在或不可执行: " + ffmpegPath);
        }
    }

    /**
     * 对视频每隔指定时间截图一次
     * @param videoPath 视频文件路径
     * @param outputDir 截图输出目录
     * @param intervalSeconds 截图时间间隔(秒)
     * @return 生成的图片文件列表
     * @throws IOException 执行过程中的IO异常
     * @throws InterruptedException 进程中断异常
     */
    public List<File> captureScreenshots(String videoPath, String outputDir, int intervalSeconds)
            throws IOException, InterruptedException {

        // 创建输出目录
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        // 获取视频总时长(秒)
        int duration = getVideoDuration(videoPath);
        if (duration <= 0) {
            throw new RuntimeException("无法获取视频时长或视频时长为0");
        }

        List<File> screenshotFiles = new ArrayList<>();

        // 计算需要截图的时间点
        for (int time = 0; time < duration; time += intervalSeconds) {
            String outputFilePath = outputDir + File.separator + "screenshot_" + time + "s.jpg";
            File screenshotFile = new File(outputFilePath);

            // 执行截图命令
            boolean success = captureSingleFrame(videoPath, outputFilePath, time);
            if (success) {
                screenshotFiles.add(screenshotFile);
                System.out.println("已生成截图: " + outputFilePath);
            }
        }

        return screenshotFiles;
    }

    /**
     * 获取视频总时长(秒)
     */
//    private int getVideoDuration(String videoPath) throws IOException, InterruptedException {
//        // 使用FFprobe获取视频信息(也可直接用ffmpeg)
//        List<String> command = new ArrayList<>();
//        command.add(ffmpegPath);
//        command.add("-i");
//        command.add(videoPath);
//        command.add("-show_entries");
//        command.add("format=duration");
//        command.add("-v");
//        command.add("quiet");
//        command.add("-of");
//        command.add("csv=p=0");
//
//        ProcessBuilder processBuilder = new ProcessBuilder(command);
//        processBuilder.redirectErrorStream(true);
//        Process process = processBuilder.start();
//
//        // 读取输出获取时长
//        try (BufferedReader reader = new BufferedReader(
//                new InputStreamReader(process.getInputStream()))) {
//
//            String durationStr = reader.readLine();
//            process.waitFor();
//
//            if (durationStr != null && !durationStr.isEmpty()) {
//                return (int) Math.ceil(Double.parseDouble(durationStr));
//            }
//        }
//
//        return 0;
//    }

    private int getVideoDuration(String videoPath) throws IOException, InterruptedException {
        // 验证视频文件是否存在
        File videoFile = new File(videoPath);
        if (!videoFile.exists() || !videoFile.canRead()) {
            throw new IOException("视频文件不存在或无法读取: " + videoPath);
        }

        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.add("-i");
        command.add(videoPath);
        command.add("-show_entries");
        command.add("format=duration"); // 只输出时长信息
        command.add("-v");
        command.add("quiet"); // 屏蔽冗余日志
        command.add("-of");
        command.add("default=noprint_wrappers=1:nokey=1"); // 仅输出时长数值

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 合并错误流到输出流（关键：避免遗漏错误信息）
        Process process = processBuilder.start();

        // 读取 FFmpeg 输出（包含时长或错误信息）
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            // 输出错误信息，便于排查（如格式不支持、文件损坏）
            throw new RuntimeException("FFmpeg 执行失败，错误信息:\n" + output.toString());
        }

        // 解析时长（可能包含小数，如 60.5 秒 → 向上取整为 61 秒）
        String durationStr = output.toString().trim();
        if (durationStr.isEmpty()) {
            throw new RuntimeException("未获取到视频时长，输出为空");
        }
        try {
            double duration = Double.parseDouble(durationStr);
            return (int) Math.ceil(duration); // 向上取整，确保最后一帧被截取
        } catch (NumberFormatException e) {
            throw new RuntimeException("解析时长失败，输出: " + durationStr, e);
        }
    }

    /**
     * 截取视频指定时间点的一帧
     */
    private boolean captureSingleFrame(String videoPath, String outputPath, int timeSeconds)
            throws IOException, InterruptedException {

        // FFmpeg截图命令
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

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // 读取输出流(防止进程阻塞)
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 可根据需要记录日志
                // System.out.println(line);
            }
        }

        // 等待进程执行完成
        int exitCode = process.waitFor();
        return exitCode == 0;
    }

    // 示例用法
    public static void main(String[] args) {
        try {
            // 替换为你的FFmpeg路径
            String ffmpegPath = "D:\\SOFTERWARE\\ShadowBot\\shadowbot-5.29.24\\ffmpeg.exe"; // Windows示例
            // String ffmpegPath = "/usr/local/bin/ffmpeg"; // Linux/Mac示例

            VideoService util = new VideoService(ffmpegPath);

            // 视频路径、输出目录、截图间隔(5秒)
            List<File> screenshots = util.captureScreenshots(
                    "D:\\SOFTERWARE\\BBDown_1.6.3_20240814_win-x64\\章晓铭逻辑22杀（2026全新，逻辑判断，适合公务员考试、事业编、选调、三支一扶；国考、省考、适合0基础和老手）\\[P1]章晓铭逻辑22杀：第1杀—选项提示思维.mp4",
                    "D:\\SOFTERWARE\\BBDown_1.6.3_20240814_win-x64\\章晓铭逻辑22杀（2026全新，逻辑判断，适合公务员考试、事业编、选调、三支一扶；国考、省考、适合0基础和老手）\\screenshots",
                    5
            );

            System.out.println("截图完成，共生成 " + screenshots.size() + " 张图片");
            for (File file : screenshots) {
                System.out.println("图片路径: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
