package com.jogyco.takeaway.config.openapi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Primary
@Configuration
@ConfigurationProperties(prefix = "openapi")
@Getter
@Setter
public class OpenApiProperties {

    private String projectTitle;
    private String projectDescription;
    private String projectVersion;

}
