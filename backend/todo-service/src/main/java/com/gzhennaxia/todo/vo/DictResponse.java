package com.gzhennaxia.todo.vo;

import java.util.List;

public class DictResponse {
    private String dictCode;
    private String version;
    private List<Item> items;

    public static class Item {
        public String key;
        public String value;
        public Integer sort;
    }

    // Getters and Setters
    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}