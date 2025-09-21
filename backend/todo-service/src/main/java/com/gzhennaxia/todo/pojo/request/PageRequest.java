package com.gzhennaxia.todo.pojo.request;

import lombok.Data;

import java.util.Map;

/**
 * @author Gzhennaxia
 */
@Data
public class PageRequest<T> {

    private long pageNo = 1;

    private long pageSize = 10;

    private Map<String, Object> query;

    private Map<String, String> sort;

}
