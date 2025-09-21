package com.gzhennaxia.todo.dto;

import lombok.Data;

@Data
public class InboxTaskCreateRequest {
    private String title;
    private String description;
    private String priority = "MEDIUM";
}