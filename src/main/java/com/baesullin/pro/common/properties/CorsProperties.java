package com.baesullin.pro.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// application.yml 에 설정해둔 cors 설정 가져오기 위한 클래스
@Getter
@Setter
@ConfigurationProperties(prefix = "cors")
@Configuration
public class CorsProperties {
    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;
    private Long maxAge;
}
