package com.personal.management.dto;

import lombok.Data;

@Data
public class InboxTaskCreateRequest {
    private String title;
    private String description;
    private String priority = "MEDIUM";
}