package com.ttl.shopping_manager_api.security;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

// 统一异常处理器
@RestControllerAdvice
public class AccessDeniedExceptionHandler {

    // 捕获权限不足异常，重新抛出，走Security的AccessDeniedHandler
    @ExceptionHandler(AccessDeniedException.class)
    public void defaultExceptionHandler(AccessDeniedException e) throws AccessDeniedException {
        throw e;
    }
}