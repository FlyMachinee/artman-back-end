package cn.edu.hit.artman.handler;

import cn.edu.hit.artman.common.exception.ArtManException;
import cn.edu.hit.artman.common.result.Result;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ArtManException.class)
    public <T> Result<T> handleArtManException(ArtManException e) {
        log.info("自定义异常: ", e);
        return new Result<>(e.getCode(), e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Result<Object> handleBindException(BindException e) {
        // 拼接错误信息，用于多个校验不通过的错误信息拼接
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message =
            allErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(";"));
        log.info("参数校验不通过：{}", message);
        return Result.badRequest("参数校验不通过：" + message);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageConversionException.class)
    public Result<Object> handleHttpMessageConversionException(HttpMessageConversionException e) {
        log.info("参数转换失败：{}", e.getMessage());
        return Result.badRequest("参数转换失败：" + e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Result<Object> handleValidationException(ValidationException e) {
        log.info("参数验证失败：{}", e.getMessage());
        return Result.badRequest("参数验证失败：" + e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public Result<Object> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.info("缺少请求头：{}", e.getMessage());
        return Result.badRequest("缺少请求头：" + e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info("方法参数类型不匹配：{}", e.getMessage());
        return Result.badRequest("方法参数类型不匹配：" + e.getMessage());
    }
}
