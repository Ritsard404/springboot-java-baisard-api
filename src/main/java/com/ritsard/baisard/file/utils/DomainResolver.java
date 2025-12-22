package com.ritsard.baisard.file.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class DomainResolver {
    public String resolveCurrentDomain() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("RequestContext is not available.");
        }
        HttpServletRequest request = attributes.getRequest();
        int port = request.getServerPort();
        boolean isDefaultPort = port == 80 || port == 443;
        return request.getScheme() + "://" + request.getServerName() + (String)(isDefaultPort ? "" : ":" + port);
    }
}

