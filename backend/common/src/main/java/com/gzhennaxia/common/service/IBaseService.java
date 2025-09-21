package com.gzhennaxia.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.common.pojo.dto.PageDto;

public interface IBaseService<T> extends IService<T> {

    IPage<T> page(PageDto<T> pageDto);

}
