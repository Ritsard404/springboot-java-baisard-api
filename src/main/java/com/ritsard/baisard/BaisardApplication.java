package com.ritsard.baisard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.ritsard")
//@EnableFeignClients(basePackages = "com.ritsard.baisard")
@EntityScan(basePackages = "com.ritsard")
@ConfigurationPropertiesScan(basePackages = "com.ritsard")
@ComponentScan(basePackages = "com.ritsard")
@SpringBootApplication
public class BaisardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaisardApplication.class, args);
	}

}
