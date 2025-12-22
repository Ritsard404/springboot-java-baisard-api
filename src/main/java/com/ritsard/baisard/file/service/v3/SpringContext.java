package com.ritsard.baisard.file.service.v3;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext has not been initialized.");
        }
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext has not been initialized.");
        }
        return context.getBean(name, clazz);
    }
}


