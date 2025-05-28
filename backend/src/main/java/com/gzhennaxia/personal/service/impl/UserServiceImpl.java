package com.gzhennaxia.personal.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.personal.dto.LoginRequest;
import com.gzhennaxia.personal.entity.User;
import com.gzhennaxia.personal.mapper.UserMapper;
import com.gzhennaxia.personal.service.UserService;
import com.gzhennaxia.personal.util.JwtUtil;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(LoginRequest loginRequest) {
        User user = getByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return jwtUtil.generateToken(user.getUsername());
    }

    @Override
    public User register(LoginRequest loginRequest) {
        // 检查用户名是否已存在
        User existingUser = getByUsername(loginRequest.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(loginRequest.getUsername());
        user.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
        user.setStatus(1); // 1: 正常状态
        user.setDeleted(0); // 0: 未删除
        user.setCreatedTime(LocalDateTime.now());

        // 保存用户
        save(user);

        // 返回用户信息（不包含密码）
        user.setPassword(null);
        return user;
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
}