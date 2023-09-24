package com.cskefu.mvc;

import com.cskefu.base.JacksonUtils;
import com.cskefu.base.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalResponseBodyHandler implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Result) {
            return body;
        }
        return Result.success(body);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<?> handleNoHandlerFoundException(HttpServletRequest request, HttpServletResponse response, NoHandlerFoundException e) {
        return Result.error(404, "访问的资源不存在：" + request.getRequestURI());
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handlerBindException(BindException e) {

        List<String> collect = new ArrayList<>();
        if (e.getBindingResult().getFieldError() != null) {
            List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
            collect = fieldErrors.stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
        }
        return Result.error(ResultEnum.ILLEGAL_ARGUMENT_EXCEPTION.getCode(), JacksonUtils.toJSONString(collect));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> collect = new ArrayList<>();
        if (e.getBindingResult().getFieldError() != null) {
            List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
            collect = fieldErrors.stream()
                    .map(item -> item.getField() + item.getDefaultMessage())
                    .collect(Collectors.toList());
        }
        return Result.error(ResultEnum.ILLEGAL_ARGUMENT_EXCEPTION.getCode(), JacksonUtils.toJSONString(collect));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handlerConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return Result.error(ResultEnum.ILLEGAL_ARGUMENT_EXCEPTION.getCode(), JacksonUtils.toJSONString(collect));
    }

    @ExceptionHandler(Throwable.class)
    public Result<?> handleThrowable(Throwable e) {
        log.error("全局异常处理[" + ResultEnum.UNKNOWN_EXCEPTION.getCode() + "]：" + e.getMessage());
        e.printStackTrace();
        return Result.error(ResultEnum.UNKNOWN_EXCEPTION.getCode(), ResultEnum.UNKNOWN_EXCEPTION.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public Result<?> handleSQLException(SQLException e) {
        log.error("全局异常处理[" + ResultEnum.SQL_EXCEPTION.getCode() + "]：" + e.getMessage());
        e.printStackTrace();
        return Result.error(ResultEnum.SQL_EXCEPTION.getCode(), ResultEnum.SQL_EXCEPTION.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public Result<?> handleDataAccessException(DataAccessException e) {
        log.error("全局异常处理[" + ResultEnum.DATA_ACCESS_EXCEPTION.getCode() + "]：" + e.getMessage());
        e.printStackTrace();
        return Result.error(ResultEnum.DATA_ACCESS_EXCEPTION.getCode(), ResultEnum.DATA_ACCESS_EXCEPTION.getMessage());
    }

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            HttpMediaTypeNotAcceptableException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
            AsyncRequestTimeoutException.class
    })
    public Result<?> handleServletException(Exception e) {
        log.error("全局异常处理[" + ResultEnum.SERVLET_EXCEPTION.getCode() + "]：" + e.getMessage());
        e.printStackTrace();
        return Result.error(ResultEnum.SERVLET_EXCEPTION.getCode(), e.getMessage());
    }
}
