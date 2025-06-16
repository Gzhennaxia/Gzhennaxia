package com.gzhennaxia.personal.controller;

import com.gzhennaxia.personal.service.FrontendService;
import com.gzhennaxia.personal.service.StartupCoordinatorService;
import com.gzhennaxia.personal.integration.ib.IBGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/startup")
@RequiredArgsConstructor
public class StartupController {

    private final StartupCoordinatorService startupCoordinatorService;
    private final IBGatewayService ibGatewayService;
    private final FrontendService frontendService;

    /**
     * 获取所有服务状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        StartupCoordinatorService.StartupStatus status = startupCoordinatorService.getStartupStatus();
        
        Map<String, Object> response = new HashMap<>();
        response.put("gatewayRunning", status.isGatewayRunning());
        response.put("frontendRunning", status.isFrontendRunning());
        response.put("allServicesRunning", status.isAllServicesRunning());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 重启所有服务
     */
    @PostMapping("/restart-all")
    public ResponseEntity<Map<String, String>> restartAll() {
        Map<String, String> response = new HashMap<>();
        try {
            startupCoordinatorService.restartAll();
            response.put("message", "所有服务重启命令已发送");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "重启失败: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 启动前端项目
     */
    @PostMapping("/frontend/start")
    public ResponseEntity<Map<String, String>> startFrontend() {
        Map<String, String> response = new HashMap<>();
        try {
            frontendService.startFrontend();
            response.put("message", "前端项目启动命令已发送");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "启动失败: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 停止前端项目
     */
    @PostMapping("/frontend/stop")
    public ResponseEntity<Map<String, String>> stopFrontend() {
        Map<String, String> response = new HashMap<>();
        try {
            frontendService.stopFrontend();
            response.put("message", "前端项目已停止");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "停止失败: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 重启前端项目
     */
    @PostMapping("/frontend/restart")
    public ResponseEntity<Map<String, String>> restartFrontend() {
        Map<String, String> response = new HashMap<>();
        try {
            frontendService.restartFrontend();
            response.put("message", "前端项目重启命令已发送");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "重启失败: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 获取前端项目状态
     */
    @GetMapping("/frontend/status")
    public ResponseEntity<Map<String, Object>> getFrontendStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("isRunning", frontendService.isFrontendRunning());
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }

    /**
     * 获取IB Gateway状态
     */
    @GetMapping("/gateway/status")
    public ResponseEntity<Map<String, Object>> getGatewayStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("isRunning", ibGatewayService.isGatewayRunning());
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }

    /**
     * 获取服务启动指南
     */
    @GetMapping("/guide")
    public ResponseEntity<Map<String, Object>> getStartupGuide() {
        Map<String, Object> guide = new HashMap<>();
        
        guide.put("title", "系统启动指南");
        guide.put("description", "完整的系统启动和使用流程");
        
        Map<String, Object> steps = new HashMap<>();
        steps.put("step1", "Spring Boot后端应用会自动启动");
        steps.put("step2", "IB Gateway会自动启动并打开浏览器(https://localhost:5000)");
        steps.put("step3", "前端项目会自动启动");
        steps.put("step4", "完成IB Gateway登录后，会自动打开前端系统(http://localhost:3000)");
        
        guide.put("steps", steps);
        
        Map<String, String> urls = new HashMap<>();
        urls.put("ibGateway", "https://localhost:5000");
        urls.put("frontend", "http://localhost:3000");
        urls.put("backend", "http://localhost:8080");
        
        guide.put("urls", urls);
        
        return ResponseEntity.ok(guide);
    }
} 