package com.ritsard.baisard.jwt.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean({MemberProperties.class})
public class MemberPropertiesImpl implements MemberProperties{
}
