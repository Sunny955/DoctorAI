package com.diagnosis_service.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.diagnosis_service.controllers.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();
        HttpServletRequest request = null;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
                break;
            }
        }

        String endpoint = request != null ? request.getRequestURI() : "Unknown Endpoint";
        String httpMethod = request != null ? request.getMethod() : "Unknown HTTP Method";
        String queryParams = request != null ? request.getQueryString() : "";

        logger.info("Request received: HTTP Method = {}, Endpoint = {}, Query Params = {}, Method = {}, Arguments = {}",
                httpMethod, endpoint, queryParams, methodName, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.info("Request completed: HTTP Method = {}, Endpoint = {}, Time taken = {} ms, Result = {}",
                    httpMethod, endpoint, duration, result);

            return result;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.error("Request failed: HTTP Method = {}, Endpoint = {}, Time taken = {} ms, Exception = {}",
                    httpMethod, endpoint, duration, e.getMessage());

            throw e;
        }
    }
}
