package com.lectoria_api.lectoria_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = {"com.lectoria_api"})
@EnableJpaRepositories(basePackages = {"com.lectoria_api"})
@EntityScan(basePackages = {"com.lectoria_api"})
@EnableFeignClients(basePackages = "com.lectoria_api.auth.infrastructure.output.client")
@SpringBootApplication
public class LectoriaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LectoriaApiApplication.class, args);
	}

}
