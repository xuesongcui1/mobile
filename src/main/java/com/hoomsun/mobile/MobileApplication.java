package com.hoomsun.mobile;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by HotWong on 2017/4/27 0027.
 */
@Configuration
@ComponentScan(basePackages={"com.hoomsun"})
@EnableAutoConfiguration
@EnableScheduling
@MapperScan("com.hoomsun.mobile.dao")
@SpringBootApplication
public class MobileApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(MobileApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(MobileApplication.class, args);
	}
}
