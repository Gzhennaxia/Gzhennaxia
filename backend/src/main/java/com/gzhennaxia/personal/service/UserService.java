package com.gzhennaxia.personal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.personal.entity.User;
import com.gzhennaxia.personal.dto.LoginRequest;

public interface UserService extends IService<User> {
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录成功返回token，失败返回null
     */
    String login(LoginRequest loginRequest);

    /**
     * 用户注册
     * @param loginRequest 注册请求
     * @return 注册成功返回用户信息
     */
    User register(LoginRequest loginRequest);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);
}