package com.personal.management.controller;

import com.personal.management.service.DictService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
     * SSE endpoint for dictionary change notifications
     */
    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String clientId) {
        SseEmitter emitter = new SseEmitter(3600000L); // 1 hour timeout
        emitters.put(clientId, emitter);

        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(() -> emitters.remove(clientId));

        return emitter;
    }

    /**
     * Notify all clients about dictionary changes
     */
    public void notifyClients(String dictCode, String newVersion) {
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

    // Other dictionary endpoints will be added here
    // based on the dictionary best practices document
}