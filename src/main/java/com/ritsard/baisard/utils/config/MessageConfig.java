package com.ritsard.baisard.utils.config;

import com.ritsard.baisard.utils.enums.ErrorCode;
import com.ritsard.baisard.utils.enums.SuccessCode;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@Primary
public class MessageConfig {
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        SuccessCode.setMessageSource((MessageSource) messageSource);
        ErrorCode.setMessageSource((MessageSource) messageSource);
        return messageSource;
    }
}

