package com.trae.openapi.common;

public class ResultUtils {
    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.success(data);
    }

    public static <T> BaseResponse<T> error(String message) {
        return BaseResponse.error(message);
    }
}