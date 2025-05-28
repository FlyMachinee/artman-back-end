package cn.edu.hit.artman.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

import static cn.edu.hit.artman.common.constant.HttpMessage.*;
import static java.net.HttpURLConnection.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class Result<T> implements Serializable {
    private Integer code;
    private String message;
    private String detail;
    private T data;

    public Result(Integer code, String detail, T data) {
        this.code = code;
        this.message = switch (code) {
            case HTTP_OK -> OK_MSG;
            case HTTP_CREATED -> CREATED_MSG;
            case HTTP_BAD_REQUEST -> BAD_REQUEST_MSG;
            case HTTP_UNAUTHORIZED -> UNAUTHORIZED_MSG;
            case HTTP_FORBIDDEN -> FORBIDDEN_MSG;
            case HTTP_NOT_FOUND -> NOT_FOUND_MSG;
            case HTTP_INTERNAL_ERROR -> INTERNAL_SERVER_ERROR_MSG;
            default -> "Unknown Status";
        };
        this.detail = detail;
        this.data = data;
    }

    public Result(Integer code, String detail) {
        this(code, detail, null);
    }

    public static <T> Result<T> ok(T data, String detail) {
        return new Result<>(HTTP_OK, detail, data);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(HTTP_OK, null, data);
    }

    public static <T> Result<T> ok(String detail) {
        return new Result<>(HTTP_OK, detail);
    }

    public static <T> Result<T> ok() {
        return new Result<>(HTTP_OK, null);
    }

    public static <T> Result<T> created() {
        return new Result<>(HTTP_CREATED, null);
    }

    public static <T> Result<T> created(String detail) {
        return new Result<>(HTTP_CREATED, detail);
    }

    public static <T> Result<T> created(T data) {
        return new Result<>(HTTP_CREATED, null, data);
    }

    public static <T> Result<T> created(T data, String detail) {
        return new Result<>(HTTP_CREATED, detail, data);
    }

    public static <T> Result<T> badRequest() {
        return new Result<>(HTTP_BAD_REQUEST, null);
    }

    public static <T> Result<T> badRequest(String detail) {
        return new Result<>(HTTP_BAD_REQUEST, detail);
    }

    public static <T> Result<T> unauthorized() {
        return new Result<>(HTTP_UNAUTHORIZED, null);
    }

    public static <T> Result<T> unauthorized(String detail) {
        return new Result<>(HTTP_UNAUTHORIZED, detail);
    }

    public static <T> Result<T> forbidden() {
        return new Result<>(HTTP_FORBIDDEN, null);
    }

    public static <T> Result<T> forbidden(String detail) {
        return new Result<>(HTTP_FORBIDDEN, detail);
    }

    public static <T> Result<T> notFound() {
        return new Result<>(HTTP_NOT_FOUND, null);
    }

    public static <T> Result<T> notFound(String detail) {
        return new Result<>(HTTP_NOT_FOUND, detail);
    }

    public static <T> Result<T> internalServerError() {
        return new Result<>(HTTP_INTERNAL_ERROR, null);
    }

    public static <T> Result<T> internalServerError(String detail) {
        return new Result<>(HTTP_INTERNAL_ERROR, detail);
    }

}
