package com.ritsard.baisard.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@OpenAPIDefinition(
        info = @Info(title = "BaiSard-Api Documentation",
                description = "BaiSard-Api Documentation",
                version = "v1"))
@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${spring.application.name}")
    private String basePath;


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        .addSecuritySchemes("refreshToken",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("cookie")
                                        .in(SecurityScheme.In.COOKIE))
                        .addHeaders("Accept-Language",
                                new Header()
                                        .description("Locale for API responses")
                                        .schema(new io.swagger.v3.oas.models.media.StringSchema()
                                                ._default("ko")))
                        .addSchemas("MultipartFile",
                                new Schema<>().type("string").format("binary")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .addSecurityItem(new SecurityRequirement().addList("refreshToken"));
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/swagger-ui/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
            }
        };
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger-ui/")
                .setViewName("forward:/swagger-ui/index.html");
    }
}
