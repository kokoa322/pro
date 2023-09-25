package com.baesullin.pro;

import com.baesullin.pro.common.properties.AppProperties;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBatchProcessing
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//@PropertySources({@PropertySource("classpath:application-key.properties")})
public class ProApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProApplication.class, args);
	}

}
