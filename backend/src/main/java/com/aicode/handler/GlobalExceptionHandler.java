package com.aicode.handler;

import com.aicode.dto.ApiResponse;
import com.aicode.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常 */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /** 文件上传过大 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ApiResponse<?> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("文件上传超过大小限制: {}", e.getMessage());
        return ApiResponse.error(413, "文件大小超过限制（最大10MB）");
    }

    /** 参数验证异常 */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ApiResponse<?> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数异常: {}", e.getMessage());
        return ApiResponse.error(400, e.getMessage());
    }

    /** 兜底异常 */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResponse<?> handleException(Exception e) {
        log.error("服务器内部错误", e);
        return ApiResponse.error("服务器内部错误");
    }
}
