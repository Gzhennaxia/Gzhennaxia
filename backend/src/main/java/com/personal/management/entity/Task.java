package com.personal.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tasks")
public class Task {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("title")
    private String title;
    
    @TableField("description")
    private String description;
    
    @TableField("start_time")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    private LocalDateTime endTime;
    
    @TableField("priority")
    private Integer priority; // 1-低, 2-中, 3-高, 4-紧急
    
    @TableField("status")
    private Integer status; // 0-待办, 1-进行中, 2-已完成, 3-已取消
    
    @TableField("category")
    private String category;
    
    @TableField("tags")
    private String tags; // JSON格式存储标签
    
    @TableField("reminder_time")
    private LocalDateTime reminderTime;
    
    @TableField("is_all_day")
    private Boolean isAllDay;
    
    @TableField("repeat_type")
    private String repeatType; // none, daily, weekly, monthly, yearly
    
    @TableField("repeat_end_date")
    private LocalDateTime repeatEndDate;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}