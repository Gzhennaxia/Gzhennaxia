package com.gzhennaxia.web;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.gzhennaxia.**.mapper")
@ComponentScan(basePackages = "com.gzhennaxia")
public class WebApplication {
    private static final Logger logger = LoggerFactory.getLogger(WebApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
