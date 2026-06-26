package com.aicode.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 统一 API 响应体
 * @param <T> 数据类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    private ApiResponse() {}

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 成功（无数据） */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }

    /** 成功（带数据） */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /** 成功（带消息和数据） */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /** 失败 */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    /** 失败（自定义状态码） */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // ---- PageResult 内部类 ----

    public static <T> PageResult<T> page(List<T> list, long total, int page, int pageSize) {
        return new PageResult<>(list, total, page, pageSize);
    }

    public static class PageResult<T> {
        private List<T> list;
        private long total;
        private int page;
        private int pageSize;

        public PageResult(List<T> list, long total, int page, int pageSize) {
            this.list = list;
            this.total = total;
            this.page = page;
            this.pageSize = pageSize;
        }

        public List<T> getList() { return list; }
        public long getTotal() { return total; }
        public int getPage() { return page; }
        public int getPageSize() { return pageSize; }
    }

    // ---- getters ----

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
