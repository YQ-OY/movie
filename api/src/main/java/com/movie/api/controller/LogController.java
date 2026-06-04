package com.movie.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证 logback 中 %X{traceId} 与日志级别输出。
 */
@Slf4j
@RestController
@Tag(name = "日志测试")
@RequestMapping("/api/log")
public class LogController {

    @GetMapping("/test")
    @Operation(summary = "日志与 traceId 测试")
    public void testLog() {
        log.info("普通业务日志");
        log.debug("调试日志");
        log.error("异常日志测试", new RuntimeException("模拟异常"));
    }
    /*验证全局异常处理*/
    @GetMapping("/test/error")
    public String testError() {
        // 故意抛异常
        int a = 1 / 0;
        return "ok";
    }
}



