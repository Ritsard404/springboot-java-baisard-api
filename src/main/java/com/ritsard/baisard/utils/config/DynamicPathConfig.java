package com.ritsard.baisard.utils.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(prefix="ritsard.path", name={"enabled"}, havingValue="true", matchIfMissing=true)
public class DynamicPathConfig
implements WebMvcConfigurer {
    @Value(value="${spring.application.name:api}")
    private String basePath;
    @Value(value="${ritsard.path.package-prefix:com.ritsard}")
    private String packagePrefix;

    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api/" + this.basePath, clazz -> clazz.getPackageName().startsWith(this.packagePrefix));
    }
}

