package com.example.ai.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

    private final ObjectMapper mapper = new ObjectMapper();

    @Around("execution(* com.example.ai.controller..*(..)) || execution(* com.example.ai.service.impl..*(..))")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        String argsJson = toJson(joinPoint.getArgs());
        log.info("➡️ Entering: {} with args={}", methodName, argsJson);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception ex) {
            log.error("❌ Exception in {}: {}", methodName, ex.getMessage(), ex);
            throw ex;
        }

        long duration = System.currentTimeMillis() - start;
        log.info("✅ Exiting: {} | Duration={} ms", methodName, duration);
        return result;
    }

    private String toJson(Object[] args) {
        try {
            return mapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            return "Unable to serialize args";
        }
    }
}
