package com.gzhennaxia.common.pojo.dto;

import lombok.Data;

import java.util.Map;


/**
 * @author gzhennaxia
 */
@Data
public class PageDto<T> {

    private long pageNo = 1;

    private long pageSize = 10;

    private Map<String, Object> query;

    private Map<String, String> sort;
}
