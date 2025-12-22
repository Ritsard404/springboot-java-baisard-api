package com.ritsard.baisard.file.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringVideoContext
implements ApplicationContextAware {
    private static ApplicationContext context;

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
    }

    public static <T> T getBean(Class<T> clazz) {
        return (T)context.getBean(clazz);
    }
}

