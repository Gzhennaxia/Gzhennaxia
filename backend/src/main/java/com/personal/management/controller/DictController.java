package com.personal.management.controller;

import com.personal.management.base.IBaseController;
import com.personal.management.base.IBaseService;
import com.personal.management.common.ApiResult;
import com.personal.management.pojo.converter.DictTypeConverter;
import com.personal.management.pojo.dto.DictTypeDto;
import com.personal.management.pojo.entity.DictType;
import com.personal.management.pojo.request.DictTypeCreateRequest;
import com.personal.management.pojo.vo.DictTypeVo;
import com.personal.management.service.DictService;
import com.personal.management.utils.DateTimeUtils;
import com.personal.management.vo.DictResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/dict")
@Tag(name = "字典管理", description = "字典的查询、新增、修改、删除接口") // 类级别分组
public class DictController extends IBaseController<DictType> {

    @Autowired
    private DictService dictService;

    @Override
    protected IBaseService<DictType> getBaseService() {
        return dictService;
    }

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 获取所有字典
     * GET /api/dicts
     */
    @GetMapping
    @Operation(
            summary = "获取所有字典",
            description = "返回系统中所有字典数据（包含所有类型），支持分页和筛选"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "查询成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResult.class) // 响应体类型为统一响应类
                    )
            ),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResult<List<DictTypeVo>> getAllDicts() {
        List<DictTypeDto> dicts = dictService.getAllDicts();
        return ApiResult.success(DictTypeConverter.convert(dicts));
    }

    /**
     * 新增字典（包含子项）
     * POST /api/dicts
     */
    @PostMapping
    @Operation(
            summary = "新增字典",
            description = "创建新的字典及其子项，字典编码唯一，子项至少包含一条"
    )
    @ApiResponse(
            responseCode = "200",
            description = "新增成功"
    )
    public DictTypeVo addDict(
            @Parameter(description = "字典新增参数（包含子项）", required = true)
            @Valid @RequestBody DictTypeDto dictDto) {
        DictTypeDto savedDict = dictService.addDict(dictDto);
        return DictTypeConverter.convert(savedDict);
    }

    /**
     * 获取单个字典
     *
     * @param code 字典编码
     * @return 字典数据响应实体
     */
    @GetMapping("/{code}")
    public ResponseEntity<DictResponse> getDict(@PathVariable String code) {
        DictResponse resp = dictService.getDict(code);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
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
    public ResponseEntity<Map<String, DictResponse>> getBatch(@RequestBody List<String> codes) {
        Map<String, DictResponse> result = dictService.getBatch(codes);
        return ResponseEntity.ok(result);
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