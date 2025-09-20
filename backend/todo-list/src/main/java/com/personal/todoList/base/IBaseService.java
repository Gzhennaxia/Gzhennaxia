package com.personal.management.base;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.personal.management.pojo.dto.PageDto;

public interface IBaseService<T> extends IService<T> {

    IPage<T> page(PageDto<T> pageDto);

}
