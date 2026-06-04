package com.movie.api.handler;

import com.movie.api.annotation.DisableBaseResponse;
import com.movie.api.interceptor.LogTraceInterceptor;
import com.movie.api.model.support.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 捕获controller异常
 * controller抛出异常执行下边的函数
 * 返回Response写入ApiResult
 */
@Slf4j
@ResponseBody
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @DisableBaseResponse
    public Object handleException(Exception e) {
        String traceId = MDC.get(LogTraceInterceptor.TRACE_ID);
        if (e instanceof AccessDeniedException) {
            log.warn("访问拒绝, traceId={}", traceId, e);
            return new ResponseResult<>(403, "你没有访问权限");
        }
        log.error("全局异常捕获, traceId={}", traceId, e);
        return new ResponseResult<>(400, e.getMessage());
    }

}
