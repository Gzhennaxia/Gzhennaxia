package com.personal.management.controller;

import com.personal.management.service.DictService;
import com.personal.management.vo.DictResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/dict")
public class DictController {
    private final DictService dictService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public DictController(DictService dictService) {
        this.dictService = dictService;
    }

    /**
     * 获取单个字典
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
     * @param codes 字典编码列表
     * @return 字典编码与版本号的映射
     */
    @PostMapping("/versions")
    public ResponseEntity<Map<String, String>> getVersions(@RequestBody List<String> codes) {
        return ResponseEntity.ok(dictService.getVersions(codes));
    }

    /**
     * 批量获取多个字典数据
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
     * @param code 字典编码
     * @return 空响应
     */
    @PostMapping("/admin/refresh/{code}")
    public ResponseEntity<Void> refreshDict(@PathVariable String code) {
        dictService.bumpVersionAndEvict(code, DictService.nextVersion());
        notifyClients(code, DictService.nextVersion());
        return ResponseEntity.ok().build();
    }

    /**
     * SSE订阅字典变更通知
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
     * @param dictCode 字典编码
     * @param newVersion 新版本号
     */
    private void notifyClients(String dictCode, String newVersion) {
        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("dict-update")
                    .data(Map.of(
                        "dictCode", dictCode,
                        "version", newVersion,
                        "timestamp", System.currentTimeMillis()
                    )));
            } catch (Exception e) {
                emitters.remove(clientId);
            }
        });
    }
}