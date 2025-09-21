package com.gzhennaxia.todo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer priority;
    private Integer status;
    private String category;
    private List<String> tags;
    private LocalDateTime reminderTime;
    private Boolean isAllDay;
    private String repeatType;
    private LocalDateTime repeatEndDate;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}