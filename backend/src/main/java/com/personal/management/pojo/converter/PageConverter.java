package com.personal.management.pojo.converter;

import com.personal.management.pojo.dto.PageDto;
import com.personal.management.pojo.request.PageRequest;

/**
 * @author Gzhennaxia
 */
public class PageConverter {

    public static <T> PageDto<T> convert(PageRequest<T> pageRequest) {
        PageDto<T> pageDto = new PageDto<>();
        pageDto.setPageNo(pageRequest.getPageNo());
        pageDto.setPageSize(pageRequest.getPageNo());
        pageDto.setQuery(pageRequest.getQuery());
        pageDto.setSort(pageRequest.getSort());
        return pageDto;
    }

}
