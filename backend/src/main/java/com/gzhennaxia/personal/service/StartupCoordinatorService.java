package com.gzhennaxia.personal.service;

import com.gzhennaxia.personal.integration.ib.IBGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartupCoordinatorService implements ApplicationRunner {

    private final IBGatewayService ibGatewayService;
    private final FrontendService frontendService;

    @Value("${startup.coordinator.enabled:true}")
    private boolean coordinatorEnabled;

    @Value("${startup.coordinator.parallel:true}")
    private boolean parallelStartup;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!coordinatorEnabled) {
            log.info("启动协调器已禁用");
            return;
        }

        log.info("🚀 开始协调启动流程...");

        if (parallelStartup) {
            // 并行启动模式：同时启动IB Gateway和前端项目
            startParallel();
        } else {
            // 顺序启动模式：先启动IB Gateway，再启动前端项目
            startSequential();
        }
    }

    /**
     * 并行启动模式
     */
    private void startParallel() {
        log.info("🔄 使用并行启动模式");

        // 异步启动IB Gateway
        CompletableFuture<Void> gatewayFuture = CompletableFuture.runAsync(() -> {
            try {
                ibGatewayService.startGateway();
            } catch (Exception e) {
                log.error("IB Gateway 启动失败", e);
            }
        }, executorService);

        // 异步启动前端项目
        CompletableFuture<Void> frontendFuture = CompletableFuture.runAsync(() -> {
            try {
                frontendService.startFrontend();
            } catch (Exception e) {
                log.error("前端项目启动失败", e);
            }
        }, executorService);

        // 等待两个服务都启动完成
        CompletableFuture.allOf(gatewayFuture, frontendFuture)
                .thenRun(() -> {
                    log.info("🎉 所有服务启动完成！");
                    printStartupSummary();
                })
                .exceptionally(throwable -> {
                    log.error("启动过程中发生错误", throwable);
                    return null;
                });
    }

    /**
     * 顺序启动模式
     */
    private void startSequential() {
        log.info("🔄 使用顺序启动模式");

        // 异步执行顺序启动
        CompletableFuture.runAsync(() -> {
            try {
                // 1. 启动IB Gateway
                log.info("📍 第一步：启动 IB Gateway");
                ibGatewayService.startGateway();

                // 2. 启动前端项目
                log.info("📍 第二步：启动前端项目");
                frontendService.startFrontend();

                log.info("🎉 顺序启动完成！");
                printStartupSummary();

            } catch (Exception e) {
                log.error("顺序启动过程中发生错误", e);
            }
        }, executorService);
    }

    /**
     * 打印启动摘要
     */
    private void printStartupSummary() {
        log.info("================== 启动摘要 ==================");
        log.info("🔗 IB Gateway 状态: {}", 
                ibGatewayService.isGatewayRunning() ? "✅ 运行中" : "❌ 未运行");
        log.info("🔗 前端项目状态: {}", 
                frontendService.isFrontendRunning() ? "✅ 运行中" : "❌ 未运行");
        log.info("============================================");
        
        if (ibGatewayService.isGatewayRunning()) {
            log.info("💡 请在浏览器中完成 IB Gateway 登录，然后使用前端系统");
        }
    }

    /**
     * 获取服务状态
     */
    public StartupStatus getStartupStatus() {
        return StartupStatus.builder()
                .gatewayRunning(ibGatewayService.isGatewayRunning())
                .frontendRunning(frontendService.isFrontendRunning())
                .allServicesRunning(ibGatewayService.isGatewayRunning() && frontendService.isFrontendRunning())
                .build();
    }

    /**
     * 重启所有服务
     */
    public void restartAll() {
        log.info("🔄 重启所有服务...");
        
        CompletableFuture.runAsync(() -> {
            try {
                // 停止所有服务
                frontendService.stopFrontend();
                ibGatewayService.stopGateway();
                
                // 等待停止完成
                Thread.sleep(5000);
                
                // 重新启动
                if (parallelStartup) {
                    startParallel();
                } else {
                    startSequential();
                }
            } catch (Exception e) {
                log.error("重启服务时发生错误", e);
            }
        }, executorService);
    }

    /**
     * 启动状态类
     */
    public static class StartupStatus {
        private boolean gatewayRunning;
        private boolean frontendRunning;
        private boolean allServicesRunning;

        public static StartupStatusBuilder builder() {
            return new StartupStatusBuilder();
        }

        public boolean isGatewayRunning() {
            return gatewayRunning;
        }

        public boolean isFrontendRunning() {
            return frontendRunning;
        }

        public boolean isAllServicesRunning() {
            return allServicesRunning;
        }

        public static class StartupStatusBuilder {
            private boolean gatewayRunning;
            private boolean frontendRunning;
            private boolean allServicesRunning;

            public StartupStatusBuilder gatewayRunning(boolean gatewayRunning) {
                this.gatewayRunning = gatewayRunning;
                return this;
            }

            public StartupStatusBuilder frontendRunning(boolean frontendRunning) {
                this.frontendRunning = frontendRunning;
                return this;
            }

            public StartupStatusBuilder allServicesRunning(boolean allServicesRunning) {
                this.allServicesRunning = allServicesRunning;
                return this;
            }

            public StartupStatus build() {
                StartupStatus status = new StartupStatus();
                status.gatewayRunning = this.gatewayRunning;
                status.frontendRunning = this.frontendRunning;
                status.allServicesRunning = this.allServicesRunning;
                return status;
            }
        }
    }
} 