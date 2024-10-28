package com.github.accountmanagementproject.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

//https://velog.io/@dhk22/Spring-AOP-%EA%B0%84%EB%8B%A8%ED%95%9C-AOP-%EC%A0%81%EC%9A%A9-%EC%98%88%EC%A0%9C-%EB%A9%94%EC%84%9C%EB%93%9C-%EC%8B%A4%ED%96%89%EC%8B%9C%EA%B0%84-%EC%B8%A1%EC%A0%95

@Slf4j
@Aspect
@Component
public class ExecutionTimer {

    @Pointcut("@annotation(com.github.accountmanagementproject.service.ExeTimer)")
    private void timer(){};

    @Around("timer()")
    public Object AssumeExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        Object result = joinPoint.proceed(); // 조인포인트의 메서드 실행
        stopWatch.stop();

        long totalTimeMillis = stopWatch.getTime();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();

        log.info("실행 메서드: {}, 실행시간 = {}ms", methodName, totalTimeMillis);
        return result;
    }
}
