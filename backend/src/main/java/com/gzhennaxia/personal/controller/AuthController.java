package com.gzhennaxia.personal.controller;

import com.gzhennaxia.personal.common.Result;
import com.gzhennaxia.personal.dto.LoginRequest;
import com.gzhennaxia.personal.entity.User;
import com.gzhennaxia.personal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.login(loginRequest);
            User user = userService.getByUsername(loginRequest.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            
            // 移除敏感信息
            user.setPassword(null);
            response.put("user", user);
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.register(loginRequest);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.success(null);
    }

    @GetMapping("/user")
    public Result<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户信息
            String username = token.replace("Bearer ", "");
            User user = userService.getByUsername(username);
            if (user != null) {
                user.setPassword(null);
                return Result.success(user);
            }
            return Result.error("用户不存在");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}