package com.gzhennaxia.todo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InboxTaskDTO {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}