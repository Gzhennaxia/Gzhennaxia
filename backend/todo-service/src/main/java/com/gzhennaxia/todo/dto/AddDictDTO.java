package com.gzhennaxia.todo.dto;

import lombok.Data;
import java.util.List;

@Data
public class AddDictDTO {
    private String code;
    private String name;
    private String version;
    private Integer status;
    private String remark;
    private List<DictItemDTO> items;

    @Data
    public static class DictItemDTO {
        private String itemKey;
        private String itemValue;
        private Integer status;
        private Integer sort;
    }
}