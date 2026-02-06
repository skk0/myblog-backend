package com.skk.blog.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 接口访问日志切面
 */
@Aspect
@Component
public class ApiLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiLogAspect.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 定义切点：所有使用 @ApiLog 注解的方法
     */
    @Pointcut("@annotation(com.skk.blog.common.ApiLog)")
    public void apiLogPointcut() {}

    /**
     * 记录方法执行前的日志
     */
    @Before("apiLogPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            ApiLog apiLog = method.getAnnotation(ApiLog.class);

            if (apiLog == null) {
                return;
            }

            // 获取请求信息
            HttpServletRequest request = getRequest();
            String requestId = generateRequestId();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

            // 构建日志信息
            Map<String, Object> logInfo = new HashMap<>();
            logInfo.put("requestId", requestId);
            logInfo.put("timestamp", timestamp);
            logInfo.put("method", request.getMethod());
            logInfo.put("uri", request.getRequestURI());
            logInfo.put("ip", getClientIp(request));
            logInfo.put("class", joinPoint.getTarget().getClass().getName());
            logInfo.put("method", method.getName());
            logInfo.put("description", apiLog.value());

            // 记录请求参数
            if (apiLog.recordParams()) {
                logInfo.put("params", getParams(joinPoint, method));
            }

            // 打印日志
            logger.info("=== API REQUEST ===");
            logger.info("RequestId: {}", requestId);
            logger.info("Timestamp: {}", timestamp);
            logger.info("Method: {} {}", request.getMethod(), request.getRequestURI());
            logger.info("ClientIP: {}", getClientIp(request));
            logger.info("Class: {}", joinPoint.getTarget().getClass().getName());
            logger.info("Method: {}", method.getName());
            logger.info("Description: {}", apiLog.value());

            if (apiLog.recordParams()) {
                logger.info("Params: {}", objectMapper.writeValueAsString(logInfo.get("params")));
            }

        } catch (Exception e) {
            logger.error("API日志记录失败", e);
        }
    }

    /**
     * 记录方法执行后的日志
     */
    @AfterReturning(pointcut = "apiLogPointcut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            ApiLog apiLog = method.getAnnotation(ApiLog.class);

            if (apiLog == null || !apiLog.recordResult()) {
                return;
            }

            logger.info("=== API RESPONSE ===");
            logger.info("Result: {}", objectMapper.writeValueAsString(result));
            logger.info("====================");

        } catch (Exception e) {
            logger.error("API响应日志记录失败", e);
        }
    }

    /**
     * 记录方法异常日志
     */
    @AfterThrowing(pointcut = "apiLogPointcut()", throwing = "ex")
    public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            logger.error("=== API ERROR ===");
            logger.error("Method: {}.{}", joinPoint.getTarget().getClass().getName(), method.getName());
            logger.error("Error: {}", ex.getMessage());
            logger.error("StackTrace: ", ex);
            logger.error("==================");

        } catch (Exception e) {
            logger.error("API异常日志记录失败", e);
        }
    }

    /**
     * 获取请求参数
     */
    private Map<String, Object> getParams(JoinPoint joinPoint, Method method) {
        Map<String, Object> params = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        if (parameters.length == 0) {
            return params;
        }

        String[] parameterNames = null;
        try {
            parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        } catch (Exception e) {
            // 如果获取不到参数名，使用 arg0, arg1...
        }

        for (int i = 0; i < parameters.length; i++) {
            String paramName = (parameterNames != null && i < parameterNames.length)
                    ? parameterNames[i]
                    : "arg" + i;

            // 过滤掉文件上传参数和敏感信息
            if (args[i] instanceof MultipartFile) {
                params.put(paramName, "[File Upload]");
                continue;
            }

            // 过滤密码等敏感字段
            String lowerName = paramName.toLowerCase();
            if (lowerName.contains("password") || lowerName.contains("token")) {
                params.put(paramName, "[REDACTED]");
                continue;
            }

            params.put(paramName, args[i]);
        }

        return params;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，第一个IP为真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取当前请求
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
    }
}
