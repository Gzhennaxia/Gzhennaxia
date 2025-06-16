package com.gzhennaxia.personal.controller;

import com.gzhennaxia.personal.integration.ib.IBGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ib-gateway")
@RequiredArgsConstructor
public class IBGatewayController {

    private final IBGatewayService ibGatewayService;

    /**
     * 获取Gateway状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("isRunning", ibGatewayService.isGatewayRunning());
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }

    /**
     * 启动Gateway
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startGateway() {
        Map<String, String> response = new HashMap<>();
        try {
            ibGatewayService.startGateway();
            response.put("message", "IB Gateway 启动命令已发送");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "启动失败: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 停止Gateway
     */
    @PostMapping("/stop")
    public ResponseEntity<Map<String, String>> stopGateway() {
        Map<String, String> response = new HashMap<>();
        try {
            ibGatewayService.stopGateway();
            response.put("message", "IB Gateway 已停止");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "停止失败: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 重启Gateway
     */
    @PostMapping("/restart")
    public ResponseEntity<Map<String, String>> restartGateway() {
        Map<String, String> response = new HashMap<>();
        try {
            ibGatewayService.restartGateway();
            response.put("message", "IB Gateway 重启命令已发送");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "重启失败: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 