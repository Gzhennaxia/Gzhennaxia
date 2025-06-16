package com.gzhennaxia.personal.integration.ib;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import org.springframework.web.client.RestTemplate;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class IBGatewayService {

    @Value("${ib.gateway.path:D:/SOFTERWARE/clientportal.beta.gw}")
    private String gatewayPath;

    @Value("${ib.gateway.enabled:true}")
    private boolean gatewayEnabled;

    @Value("${ib.gateway.startup.timeout:30}")
    private int startupTimeoutSeconds;

    @Value("${ib.api.gateway.url:https://localhost:5000}")
    private String gatewayUrl;

    @Value("${ib.gateway.auto.open.browser:true}")
    private boolean autoOpenBrowser;

    private Process gatewayProcess;
    private final RestTemplate restTemplate;

    public IBGatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 启动 IB Gateway
     */
    public void startGateway() {
        try {
            log.info("正在启动 IB Gateway...");
            
            // 检查路径是否存在
            File gatewayDir = new File(gatewayPath);
            if (!gatewayDir.exists()) {
                log.error("IB Gateway 路径不存在: {}", gatewayPath);
                return;
            }

            // 检查批处理文件是否存在
            File runBat = new File(gatewayDir, "bin/run.bat");
            if (!runBat.exists()) {
                log.error("run.bat 文件不存在: {}", runBat.getAbsolutePath());
                return;
            }

            // 构建启动命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                "cmd.exe", "/c", "bin\\run.bat", "root\\conf.yaml"
            );
            processBuilder.directory(gatewayDir);
            processBuilder.redirectErrorStream(true);

            // 启动进程
            gatewayProcess = processBuilder.start();
            
            log.info("IB Gateway 进程已启动，进程ID: {}", gatewayProcess.pid());
            
            // 轮询检查Gateway启动状态
            boolean started = waitForGatewayStartup();
            
            if (started) {
                log.info("IB Gateway 启动成功，请在浏览器中访问 {} 进行登录", gatewayUrl);
                
                // 自动打开浏览器
                if (autoOpenBrowser) {
                    openBrowser(gatewayUrl);
                }
            } else {
                log.error("IB Gateway 启动超时或失败");
                stopGateway();
            }
            
        } catch (IOException | InterruptedException e) {
            log.error("启动 IB Gateway 时发生错误", e);
        }
    }

    /**
     * 等待Gateway启动成功
     * 每5秒检查一次，直到启动成功或超时
     */
    private boolean waitForGatewayStartup() throws InterruptedException {
        int maxAttempts = Math.max(startupTimeoutSeconds / 5, 1); // 每5秒检查一次，至少检查1次
        int attempts = 0;
        
        log.info("开始检查 IB Gateway 启动状态，最多等待 {} 秒", startupTimeoutSeconds);
        
        // 初始等待5秒，让Gateway有时间启动
        Thread.sleep(5000);
        
        while (attempts < maxAttempts) {
            attempts++;
            
            // 检查进程是否还在运行
            if (gatewayProcess == null || !gatewayProcess.isAlive()) {
                log.error("IB Gateway 进程已退出");
                return false;
            }
            
            // 检查Gateway健康状态
            if (checkGatewayHealth()) {
                log.info("IB Gateway 健康检查通过，启动成功！总等待时间: {} 秒", attempts * 5 + 5);
                return true;
            }
            
            log.info("第 {} 次检查：IB Gateway 尚未就绪，继续等待...", attempts);
            
            // 如果不是最后一次检查，则继续等待
            if (attempts < maxAttempts) {
                Thread.sleep(5000); // 等待5秒
            }
        }
        
        log.warn("IB Gateway 启动超时，已等待 {} 秒", startupTimeoutSeconds);
        return false;
    }

    /**
     * 检查Gateway健康状态
     */
    private boolean checkGatewayHealth() {
        try {
            // 尝试访问Gateway的健康检查端点 - 使用POST方法
            String healthUrl = gatewayUrl + "/v1/api/iserver/auth/status";
            
            // 创建请求头（根据IBKR API文档建议）
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "IBGateway-HealthCheck/1.0");
            headers.set("Accept", "*/*");
            headers.set("Connection", "keep-alive");
            
            // 创建空的请求体（根据IBKR API文档要求）
            String requestBody = "{}";
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);
            
            // 发送POST请求
            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(healthUrl, entity, String.class);
            
            // 检查响应状态码是否为200
            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Gateway健康检查成功: {}", response.getStatusCode());
                return true;
            } else {
                log.debug("Gateway健康检查失败，状态码: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            // 连接失败说明Gateway还没启动完成
            log.debug("Gateway健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 停止 IB Gateway
     */
    public void stopGateway() {
        if (gatewayProcess != null && gatewayProcess.isAlive()) {
            log.info("正在停止 IB Gateway...");
            gatewayProcess.destroyForcibly();
            try {
                gatewayProcess.waitFor(10, TimeUnit.SECONDS);
                log.info("IB Gateway 已停止");
            } catch (InterruptedException e) {
                log.error("等待 IB Gateway 停止时被中断", e);
            }
        }
    }

    /**
     * 检查 Gateway 是否运行
     */
    public boolean isGatewayRunning() {
        return gatewayProcess != null && gatewayProcess.isAlive();
    }

    /**
     * 重启 Gateway
     */
    public void restartGateway() {
        stopGateway();
        try {
            Thread.sleep(5000); // 等待5秒确保完全停止
            startGateway();
        } catch (InterruptedException e) {
            log.error("重启 Gateway 时被中断", e);
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
                    log.info("已自动打开浏览器访问: {}", url);
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
                log.info("已通过命令行打开浏览器访问: {}", url);
            }
        } catch (Exception e) {
            log.error("打开浏览器失败: {}", e.getMessage());
        }
    }

    /**
     * 应用关闭时自动停止Gateway
     */
    @PreDestroy
    public void cleanup() {
        log.info("应用正在关闭，停止 IB Gateway...");
        stopGateway();
    }
} 