package com.personal.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PersonalManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonalManagementApplication.class, args);
    }
}