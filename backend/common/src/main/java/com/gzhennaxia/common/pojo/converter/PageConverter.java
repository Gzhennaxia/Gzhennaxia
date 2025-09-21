package com.gzhennaxia.common.pojo.converter;

import com.gzhennaxia.common.pojo.dto.PageDto;
import com.gzhennaxia.common.pojo.request.PageRequest;

/**
 * @author Gzhennaxia
 */
public class PageConverter {

    public static <T> PageDto<T> convert(PageRequest<T> pageRequest) {
        PageDto<T> pageDto = new PageDto<>();
        pageDto.setPageNo(pageRequest.getPageNo());
        pageDto.setPageSize(pageRequest.getPageSize());
        pageDto.setQuery(pageRequest.getQuery());
        pageDto.setSort(pageRequest.getSort());
        return pageDto;
    }

}
