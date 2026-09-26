package com.lectoria_api.auth.infrastructure.output.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignAuthClientConfig {

    @Bean
    public Encoder feignAuthClientFormEncoder() {
        return new SpringFormEncoder();
    }

}
