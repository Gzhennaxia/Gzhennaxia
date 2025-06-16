package com.gzhennaxia.personal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PreDestroy;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FrontendService {

    @Value("${frontend.enabled:true}")
    private boolean frontendEnabled;

    @Value("${frontend.path:frontend}")
    private String frontendPath;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${frontend.startup.timeout:60}")
    private int startupTimeoutSeconds;

    @Value("${frontend.auto.open.browser:true}")
    private boolean autoOpenBrowser;

    private Process frontendProcess;
    private final RestTemplate restTemplate;

    public FrontendService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 启动前端项目
     */
    public void startFrontend() {
        if (!frontendEnabled) {
            log.info("前端自动启动已禁用");
            return;
        }

        try {
            log.info("正在启动前端项目...");

            // 检查前端项目路径是否存在
            File frontendDir = resolveFrontendPath();
            if (!frontendDir.exists()) {
                log.error("前端项目路径不存在: {}", frontendDir.getAbsolutePath());
                return;
            }

            // 检查package.json文件是否存在
            File packageJson = new File(frontendDir, "package.json");
            if (!packageJson.exists()) {
                log.error("package.json 文件不存在: {}", packageJson.getAbsolutePath());
                return;
            }

            // 构建启动命令
            ProcessBuilder processBuilder = createProcessBuilder();
            processBuilder.directory(frontendDir);
            processBuilder.redirectErrorStream(false);

            // 启动进程
            frontendProcess = processBuilder.start();
            
            log.info("前端项目进程已启动，进程ID: {}", frontendProcess.pid());
            
            // 轮询检查前端启动状态
            boolean started = waitForFrontendStartup();
            
            if (started) {
                log.info("前端项目启动成功，访问地址: {}", frontendUrl);
                
                // 自动打开浏览器
                if (autoOpenBrowser) {
                    // 延迟3秒再打开前端，确保用户先完成IB Gateway登录
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            openBrowser(frontendUrl);
                        } catch (InterruptedException e) {
                            log.error("延迟打开前端浏览器时被中断", e);
                        }
                    }).start();
                }
            } else {
                log.error("前端项目启动超时或失败");
                stopFrontend();
            }
            
        } catch (IOException e) {
            log.error("启动前端项目时发生错误", e);
        }
    }

    /**
     * 解析前端项目路径
     */
    private File resolveFrontendPath() {
        File frontendDir = new File(frontendPath);
        
        // 如果是绝对路径，直接返回
        if (frontendDir.isAbsolute()) {
            return frontendDir;
        }
        
        // 如果是相对路径，尝试相对于当前工作目录
        if (frontendDir.exists()) {
            return frontendDir;
        }
        
        // 如果当前工作目录下不存在，尝试相对于项目根目录
        // 获取当前工作目录
        String currentDir = System.getProperty("user.dir");
        File currentDirFile = new File(currentDir);
        
        // 如果当前在backend目录下，则向上一级寻找
        if (currentDirFile.getName().equals("backend")) {
            File projectRoot = currentDirFile.getParentFile();
            File frontendInProjectRoot = new File(projectRoot, frontendPath);
            if (frontendInProjectRoot.exists()) {
                return frontendInProjectRoot;
            }
        }
        
        // 如果都找不到，返回原始相对路径（保持原有行为）
        return frontendDir;
    }

    /**
     * 创建进程构建器（根据操作系统选择合适的命令）
     */
    private ProcessBuilder createProcessBuilder() {
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            return new ProcessBuilder("cmd.exe", "/c", "npm run dev");
        } else {
            return new ProcessBuilder("npm", "run", "dev");
        }
    }

    /**
     * 等待前端启动成功
     */
    private boolean waitForFrontendStartup() {
        int maxAttempts = Math.max(startupTimeoutSeconds / 10, 1); // 每10秒检查一次
        int attempts = 0;
        
        log.info("开始检查前端项目启动状态，最多等待 {} 秒", startupTimeoutSeconds);
        
        // 初始等待10秒，让前端有时间启动
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            return false;
        }
        
        while (attempts < maxAttempts) {
            attempts++;
            
            // 检查进程是否还在运行
            if (frontendProcess == null || !frontendProcess.isAlive()) {
                log.error("前端项目进程已退出");
                return false;
            }
            
            // 检查前端健康状态
            if (checkFrontendHealth()) {
                log.info("前端项目健康检查通过，启动成功！总等待时间: {} 秒", attempts * 10 + 10);
                return true;
            }
            
            log.info("第 {} 次检查：前端项目尚未就绪，继续等待...", attempts);
            
            // 如果不是最后一次检查，则继续等待
            if (attempts < maxAttempts) {
                try {
                    Thread.sleep(10000); // 等待10秒
                } catch (InterruptedException e) {
                    return false;
                }
            }
        }
        
        log.warn("前端项目启动超时，已等待 {} 秒", startupTimeoutSeconds);
        return false;
    }

    /**
     * 检查前端健康状态
     */
    private boolean checkFrontendHealth() {
        try {
            // 尝试访问前端首页
            restTemplate.getForObject(frontendUrl, String.class);
            return true;
        } catch (Exception e) {
            // 连接失败说明前端还没启动完成
            log.debug("前端健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 停止前端项目
     */
    public void stopFrontend() {
        if (frontendProcess != null && frontendProcess.isAlive()) {
            log.info("正在停止前端项目...");
            frontendProcess.destroyForcibly();
            try {
                frontendProcess.waitFor(10, TimeUnit.SECONDS);
                log.info("前端项目已停止");
            } catch (InterruptedException e) {
                log.error("等待前端项目停止时被中断", e);
            }
        }
    }

    /**
     * 检查前端是否运行
     */
    public boolean isFrontendRunning() {
        return frontendProcess != null && frontendProcess.isAlive();
    }

    /**
     * 重启前端项目
     */
    public void restartFrontend() {
        stopFrontend();
        try {
            Thread.sleep(3000); // 等待3秒确保完全停止
            startFrontend();
        } catch (InterruptedException e) {
            log.error("重启前端项目时被中断", e);
        }
    }

    /**
     * 打开默认浏览器
     */
    private void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                    log.info("已自动打开浏览器访问前端: {}", url);
                } else {
                    log.warn("当前系统不支持浏览器操作");
                }
            } else {
                // 尝试使用命令行打开浏览器
                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder pb;
                
                if (os.contains("win")) {
                    pb = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
                } else if (os.contains("mac")) {
                    pb = new ProcessBuilder("open", url);
                } else {
                    pb = new ProcessBuilder("xdg-open", url);
                }
                
                pb.start();
                log.info("已通过命令行打开浏览器访问前端: {}", url);
            }
        } catch (Exception e) {
            log.error("打开浏览器访问前端失败: {}", e.getMessage());
        }
    }

    /**
     * 应用关闭时自动停止前端项目
     */
    @PreDestroy
    public void cleanup() {
        log.info("应用正在关闭，停止前端项目...");
        stopFrontend();
    }
} 