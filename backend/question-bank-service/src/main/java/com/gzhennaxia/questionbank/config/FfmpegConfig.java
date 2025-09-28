package com.gzhennaxia.questionbank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.gzhennaxia.question.bank.service.VideoService;

@Configuration
public class FfmpegConfig {

    @Value("${app.ffmpeg.path}")
    private String ffmpegPath;

    @Bean
    public VideoService videoService() {
        return new VideoService(ffmpegPath);
    }
}