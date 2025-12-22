package com.ritsard.baisard.utils.config;

import java.lang.reflect.Method;

import com.ritsard.baisard.utils.dto.ApiResponse;
import com.ritsard.baisard.utils.enums.SuccessCode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@Aspect
public class AutoCrudResponseAspect {
    private static final Logger log = LoggerFactory.getLogger(AutoCrudResponseAspect.class);

    @AfterReturning(pointcut = "execution(* com.lodong..controller..*(..)) && (@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.PatchMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping))", returning = "result")
    public void processSuccessResponse(JoinPoint joinPoint, Object result) {
        log.debug("\ud83d\udd04 AOP \uc131\uacf5 \uc751\ub2f5 \ucc98\ub9ac \uc2dc\uc791: {}.{}", (Object) joinPoint.getTarget().getClass().getSimpleName(), (Object) joinPoint.getSignature().getName());
        try {
            ApiResponse<?> apiResponse = this.extractApiResponse(result);
            if (apiResponse != null && this.shouldAutoProcess(apiResponse)) {
                SuccessCode successCode = this.determineSuccessCode(joinPoint);
                apiResponse.setStatus(successCode.getStatus());
                apiResponse.setResultMsg(successCode.getMessage());
                log.debug("\u2705 \uc790\ub3d9 CRUD \ucc98\ub9ac \uc644\ub8cc: {} -> {} ({})", new Object[]{this.getHttpMethod(joinPoint), successCode.name(), successCode.getMessage()});
            } else {
                log.debug("\u23ed\ufe0f \uc790\ub3d9 \ucc98\ub9ac \uc2a4\ud0b5: \uc774\ubbf8 \ucc98\ub9ac\ub41c \uc751\ub2f5 \ub610\ub294 \uc870\uac74 \ubd88\ub9cc\uc871");
            }
        } catch (Exception e) {
            log.warn("\u26a0\ufe0f AOP \ucc98\ub9ac \uc911 \uc624\ub958 \ubc1c\uc0dd: {}", (Object) e.getMessage());
        }
    }

    private ApiResponse<?> extractApiResponse(Object result) {
        if (result instanceof ResponseEntity) {
            ResponseEntity responseEntity = (ResponseEntity) result;
            Object body = responseEntity.getBody();
            return body instanceof ApiResponse ? (ApiResponse) body : null;
        }
        if (result instanceof ApiResponse) {
            return (ApiResponse) result;
        }
        return null;
    }

    private boolean shouldAutoProcess(ApiResponse<?> response) {
        return response.isSuccess() && response.getStatus() == 200 && (response.getResultMsg() == null || response.getResultMsg().isEmpty() || "\uc131\uacf5".equals(response.getResultMsg()));
    }

    private SuccessCode determineSuccessCode(JoinPoint joinPoint) {
        String httpMethod = this.getHttpMethod(joinPoint);
        return switch (httpMethod.toUpperCase()) {
            case "GET" -> SuccessCode.SELECT_SUCCESS;
            case "POST" -> SuccessCode.INSERT_SUCCESS;
            case "PUT", "PATCH" -> SuccessCode.UPDATE_SUCCESS;
            case "DELETE" -> SuccessCode.DELETE_SUCCESS;
            default -> SuccessCode.SELECT_SUCCESS;
        };
    }

    private String getHttpMethod(JoinPoint joinPoint) {
        RequestMapping requestMapping;
        RequestMethod[] methods;
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (method.isAnnotationPresent(GetMapping.class)) {
            return "GET";
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            return "POST";
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            return "PUT";
        }
        if (method.isAnnotationPresent(PatchMapping.class)) {
            return "PATCH";
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return "DELETE";
        }
        if (method.isAnnotationPresent(RequestMapping.class) && (methods = (requestMapping = method.getAnnotation(RequestMapping.class)).method()).length > 0) {
            return methods[0].name();
        }
        return "GET";
    }
}

