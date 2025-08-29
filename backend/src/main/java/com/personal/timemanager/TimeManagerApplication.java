package com.personal.timemanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gzhennaxia.timemanager.mapper")
public class TimeManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TimeManagerApplication.class, args);
    }
}