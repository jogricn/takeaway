package com.jogyco.takeaway;

import com.jogyco.takeaway.config.openapi.OpenApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OpenApiProperties.class)
public class TakeawayApplication {
    public static void main(String[] args) {
        SpringApplication.run(TakeawayApplication.class, args);
    }
}