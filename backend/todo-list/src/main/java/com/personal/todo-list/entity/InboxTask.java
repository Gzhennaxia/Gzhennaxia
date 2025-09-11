package com.personal.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inbox_task")
public class InboxTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String priority;
    private Integer status;
    private Integer deleted;
    private String remark;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
