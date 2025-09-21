package com.gzhennaxia.todo.base;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.todo.pojo.dto.PageDto;

public interface IBaseService<T> extends IService<T> {

    IPage<T> page(PageDto<T> pageDto);

}
