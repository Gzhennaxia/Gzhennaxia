package com.personal.management.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.personal.management.common.ApiResponse;
import com.personal.management.pojo.converter.PageConverter;
import com.personal.management.pojo.dto.PageDto;
import com.personal.management.pojo.request.PageRequest;
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
