package com.gzhennaxia.common.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gzhennaxia.common.pojo.converter.PageConverter;
import com.gzhennaxia.common.pojo.dto.PageDto;
import com.gzhennaxia.common.pojo.request.PageRequest;
import com.gzhennaxia.common.service.IBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class IBaseController<T> {

    protected abstract IBaseService<T> getBaseService();

    /**
     * 分页查询接口
     *
     * @param pageRequest 分页请求参数，包含分页信息和查询条件
     * @return 分页查询结果，包含数据列表和分页信息
     */
    @Operation(summary = "分页查询", description = "根据分页参数和查询条件进行分页查询")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IPage.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/page")
    public IPage<T> page(@RequestBody PageRequest<T> pageRequest) {
        PageDto<T> pageDto = PageConverter.convert(pageRequest);
        return getBaseService().page(pageDto);
    }

}
