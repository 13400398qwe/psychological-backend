package com.example.scupsychological.common.handler;

import com.example.scupsychological.common.Result;
import com.example.scupsychological.common.exception.BaseException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@io.swagger.v3.oas.annotations.Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 在你的 GlobalExceptionHandler.java 中

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.OK) // 无论业务成功或失败，HTTP 状态码通常返回 200
    public Result<Void> handleBusinessException(BaseException e) {
        log.error("异常信息：{}", e.getMessage());
        return Result.error(e.getMessage());
    }
}
