package com.gzhennaxia.common.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gzhennaxia.common.pojo.converter.PageConverter;
import com.gzhennaxia.common.pojo.dto.PageDto;
import com.gzhennaxia.common.pojo.request.PageRequest;
import com.gzhennaxia.common.service.IBaseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class IBaseController<T> {

    protected abstract IBaseService<T> getBaseService();

    // dictId
    @PostMapping("/page")
    public IPage<T> page(@RequestBody PageRequest<T> pageRequest) {
        PageDto<T> pageDto = PageConverter.convert(pageRequest);
        return getBaseService().page(pageDto);
    }

}
