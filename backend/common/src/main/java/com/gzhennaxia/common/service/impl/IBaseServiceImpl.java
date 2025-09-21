package com.gzhennaxia.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.common.pojo.dto.PageDto;
import com.gzhennaxia.common.service.IBaseService;

import java.util.Map;
import java.util.Optional;

/**
 * @author Gzhennaxia
 */
public abstract class IBaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {

    @Override
    public IPage<T> page(PageDto<T> pageDto) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();

        if (pageDto != null) {
            Map<String, Object> queryParams = pageDto.getQuery();
            if (queryParams != null && !queryParams.isEmpty()) {
                for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    String[] s = key.split("_");
                    String fieldName = StringUtils.camelToUnderline(s[0]);
                    String opt = "eq";
                    if (s.length > 1) {
                        opt = Optional.ofNullable(s[1]).orElse("eq");
                    }
                    switch (opt) {
                        case "eq":
                            wrapper.eq(fieldName, value);
                            break;
                        case "ne":
                            wrapper.ne(fieldName, value);
                            break;
                        case "gt":
                            wrapper.gt(fieldName, value);
                            break;
                        case "ge":
                            wrapper.ge(fieldName, value);
                            break;
                        case "lt":
                            wrapper.lt(fieldName, value);
                            break;
                        case "le":
                            wrapper.le(fieldName, value);
                            break;
                        case "like":
                            wrapper.like(fieldName, value);
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported operator");
                    }
                }
            }
            Map<String, String> sort = pageDto.getSort();
            if (sort != null && !sort.isEmpty()) {
                for (Map.Entry<String, String> entry : sort.entrySet()) {
                    String fieldName = StringUtils.camelToUnderline(entry.getKey());
                    wrapper.orderBy(true, "asc".equalsIgnoreCase(entry.getValue()), fieldName);
                }
            }
        }
        return getBaseMapper().selectPage(new Page<>(pageDto.getPageNo(), pageDto.getPageSize()), wrapper);
    }

}
