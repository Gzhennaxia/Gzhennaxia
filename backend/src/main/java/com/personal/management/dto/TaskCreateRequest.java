package com.personal.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskCreateRequest {
    
    @NotBlank(message = "任务标题不能为空")
    private String title;
    
    private String description;
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer priority = 2; // 默认中等优先级
    
    private String category;
    
    private List<String> tags;
    
    private LocalDateTime reminderTime;
    
    private Boolean isAllDay = false;
    
    private String repeatType = "none";
    
    private LocalDateTime repeatEndDate;
}