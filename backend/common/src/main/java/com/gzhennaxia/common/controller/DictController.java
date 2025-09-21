package com.gzhennaxia.common.controller;

import com.gzhennaxia.common.pojo.dto.DictDto;
import com.gzhennaxia.common.pojo.entity.Dict;
import com.gzhennaxia.common.pojo.vo.ApiResult;
import com.gzhennaxia.common.service.DictService;
import com.gzhennaxia.common.service.IBaseService;
import com.gzhennaxia.common.utils.DateTimeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gzhennaxia
 */
@RestController
@RequestMapping("/api/dict")
@Tag(name = "字典管理", description = "字典的查询、新增、修改、删除接口")
public class DictController extends IBaseController<Dict> {

    @Autowired
    private DictService dictService;

    @Override
    protected IBaseService<Dict> getBaseService() {
        return dictService;
    }

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 获取所有字典
     * GET /api/dicts
     */
    @GetMapping
    @Operation(summary = "获取所有字典", description = "返回系统中所有字典数据（包含所有类型），支持分页和筛选")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public List<DictDto> getAllDicts() {
        return dictService.getAllDicts();
    }

    /**
     * 新增字典（包含子项）
     * POST /api/dicts
     */
    @PostMapping
    @Operation(summary = "新增字典", description = "创建新的字典及其子项，字典编码唯一，子项至少包含一条")
    @ApiResponse(responseCode = "200", description = "新增成功")
    public DictDto addDict(@Parameter(description = "字典新增参数（包含子项）", required = true) @Valid @RequestBody DictDto dictDto) {
        dictDto.setVersion(DateTimeUtils.nextVersion());
        DictDto result = dictService.addDict(dictDto);
        // 通知客户端字典变更
        notifyClients(result.getDictCode(), String.valueOf(result.getVersion()));
        return result;
    }

    /**
     * 获取单个字典
     *
     * @param dictCode 字典编码
     * @return 字典数据响应实体
     */
    @GetMapping("/{dictCode}")
    public DictDto getDict(@PathVariable String dictCode) {
        return dictService.getDict(dictCode);
    }

    /**
     * 修改字典
     * PUT /api/dict/{code}
     */
    @PutMapping("/{code}")
    @Operation(summary = "修改字典", description = "更新指定字典及其子项，版本号自动递增")
    @ApiResponse(responseCode = "200", description = "修改成功")
    public DictDto updateDict(
            @PathVariable String code,
            @Parameter(description = "字典修改参数（包含子项）", required = true)
            @Valid @RequestBody DictDto dictDto) {
        // 修改时版本号
        dictDto.setVersion(DateTimeUtils.nextVersion());
        dictDto.setDictCode(code); // 确保编码一致
        DictDto result = dictService.updateDict(dictDto);
        // 通知客户端字典变更
        notifyClients(result.getDictCode(), String.valueOf(result.getVersion()));
        return result;
    }

    /**
     * 批量获取字典版本号
     *
     * @param codes 字典编码列表
     * @return 字典编码与版本号的映射
     */
    @PostMapping("/versions")
    public ResponseEntity<Map<String, String>> getVersions(@RequestBody List<String> codes) {
        return ResponseEntity.ok(dictService.getVersions(codes));
    }

    /**
     * 批量获取多个字典数据
     *
     * @param codes 字典编码列表
     * @return 字典编码与字典数据的映射
     */
    @PostMapping("/batch")
    public Map<String, DictDto> getBatch(@RequestBody List<String> codes) {
        return dictService.getBatch(codes);
    }

    /**
     * 变更字典版本号（管理端接口）
     *
     * @param code 字典编码
     * @return 空响应
     */
    @PostMapping("/admin/refresh/{code}")
    public ResponseEntity<Void> refreshDict(@PathVariable String code) {
        dictService.bumpVersionAndEvict(code, DateTimeUtils.nextVersion());
        notifyClients(code, DateTimeUtils.nextVersion());
        return ResponseEntity.ok().build();
    }

    /**
     * SSE订阅字典变更通知
     *
     * @param clientId 客户端唯一标识
     * @return SSE事件流
     */
    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String clientId) {
        SseEmitter emitter = new SseEmitter(3600000L); // 1小时超时
        emitters.put(clientId, emitter);

        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(() -> emitters.remove(clientId));

        return emitter;
    }

    /**
     * 更新字典状态
     */
    @PutMapping("/{code}/status")
    @Operation(summary = "更新字典状态", description = "启用或禁用指定的字典")
    public ResponseEntity<ApiResult<Void>> updateDictStatus(
            @PathVariable String code,
            @RequestBody Map<String, Integer> statusMap) {
        try {
            Integer status = statusMap.get("status");
            dictService.updateDictStatus(code, status);
            return ResponseEntity.ok(ApiResult.success(null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResult.error(500, "状态更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除字典（软删除）
     */
    @DeleteMapping("/{code}")
    @Operation(summary = "删除字典", description = "软删除指定的字典，不会物理删除数据")
    public void deleteDict(@PathVariable String code) {
        dictService.softDeleteDict(code);
    }

    /**
     * 通知所有客户端字典变更
     *
     * @param dictCode   字典编码
     * @param newVersion 新版本号
     */
    private void notifyClients(String dictCode, String newVersion) {
        emitters.forEach((clientId, emitter) -> {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("dictCode", dictCode);
                data.put("version", newVersion);
                data.put("timestamp", System.currentTimeMillis());

                emitter.send(SseEmitter.event()
                        .name("dict-update")
                        .data(data));
            } catch (Exception e) {
                emitters.remove(clientId);
            }
        });
    }

}