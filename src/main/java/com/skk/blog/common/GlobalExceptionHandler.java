package com.skk.blog.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器 - 统一使用 Result 格式返回
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 获取请求路径
     */
    private String getPath(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : "";
    }

    /**
     * 参数校验异常 (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Map<String, String>> handleValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "字段值无效",
                        (existing, replacement) -> existing
                ));

        logger.warn("参数校验失败: {} - {}", getPath(request), errors);

        Result<Map<String, String>> result = new Result<>();
        result.setCode(400);
        result.setMessage("参数校验失败");
        result.setData(errors);
        return result;
    }

    /**
     * 参数绑定异常 (400)
     */
    @ExceptionHandler(BindException.class)
    public Result<Map<String, String>> handleBindException(BindException e, HttpServletRequest request) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "绑定失败",
                        (existing, replacement) -> existing
                ));

        logger.warn("参数绑定失败: {} - {}", getPath(request), errors);

        Result<Map<String, String>> result = new Result<>();
        result.setCode(400);
        result.setMessage("参数绑定失败");
        result.setData(errors);
        return result;
    }

    /**
     * 认证异常 (401)
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public Result<Void> handleAuthenticationException(org.springframework.security.core.AuthenticationException e, HttpServletRequest request) {
        logger.warn("认证失败: {} - {}", getPath(request), e.getMessage());

        Result<Void> result = new Result<>();
        if (e instanceof org.springframework.security.authentication.BadCredentialsException) {
            result.setCode(401);
            result.setMessage("用户名或密码错误");
        } else if (e instanceof org.springframework.security.core.userdetails.UsernameNotFoundException) {
            result.setCode(401);
            result.setMessage("用户不存在");
        } else {
            result.setCode(401);
            result.setMessage("认证失败，请重新登录");
        }
        return result;
    }

    /**
     * 权限不足异常 (403)
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e, HttpServletRequest request) {
        logger.warn("权限不足: {} - {}", getPath(request), e.getMessage());

        Result<Void> result = new Result<>();
        result.setCode(403);
        result.setMessage("没有操作权限");
        return result;
    }

    /**
     * 请求方法不支持异常 (405)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        logger.warn("不支持的请求方法: {} - {} - {}", getPath(request), e.getMethod(), e.getMessage());

        Result<Void> result = new Result<>();
        result.setCode(405);
        result.setMessage("不支持的请求方法: " + e.getMethod());
        return result;
    }

    /**
     * 404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Void> handleNotFound(NoHandlerFoundException e, HttpServletRequest request) {
        logger.warn("资源不存在: {} - {}", getPath(request), e.getMessage());

        Result<Void> result = new Result<>();
        result.setCode(404);
        result.setMessage("请求的资源不存在");
        return result;
    }

    /**
     * 非法参数异常 (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        logger.warn("非法参数: {} - {}", getPath(request), e.getMessage());

        Result<Void> result = new Result<>();
        result.setCode(400);
        result.setMessage(e.getMessage());
        return result;
    }

    /**
     * 运行时异常 (400)
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.error("运行时异常: {} - {}", getPath(request), e.getMessage(), e);

        Result<Void> result = new Result<>();
        result.setCode(400);
        result.setMessage(e.getMessage());
        return result;
    }

    /**
     * 其他所有异常 (500)
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        logger.error("系统异常: {} - {}", getPath(request), e.getMessage(), e);

        Result<Void> result = new Result<>();
        result.setCode(500);
        result.setMessage("服务器内部错误，请稍后重试");
        return result;
    }
}
