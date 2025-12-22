package com.ritsard.baisard.base.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration(
        after = {HibernateJpaAutoConfiguration.class}
)
@ConditionalOnClass(
        name = {"org.springframework.data.jpa.repository.JpaRepository", "jakarta.persistence.Entity"}
)
@ComponentScan(
        basePackages = {"com.ritsard.baisard"}
)
public class BaseModuleAutoConfiguration {
    @Bean
    @ConditionalOnClass(
            name = {"com.querydsl.jpa.impl.JPAQueryFactory"}
    )
    @ConditionalOnMissingBean({JPAQueryFactory.class})
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    @ConditionalOnMissingBean({BaseModuleConfigurer.class})
    public BaseModuleConfigurer defaultBaseModuleConfigurer() {
        return new DefaultBaseModuleConfigurer();
    }

    @Bean(
            name = {"baseModuleNamingStrategy"}
    )
    @ConditionalOnClass(
            name = {"org.hibernate.boot.model.naming.PhysicalNamingStrategy"}
    )
    @ConditionalOnMissingBean(
            name = {"baseModuleNamingStrategy"}
    )
    public SimpleNamingStrategy baseModuleNamingStrategy(BaseModuleConfigurer configurer) {
        return new SimpleNamingStrategy(configurer);
    }
}
