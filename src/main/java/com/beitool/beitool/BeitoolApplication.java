package com.beitool.beitool;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BeitoolApplication {
	//두 개의 설정 파일 적용
	public static final String APPLICATION_LOCATIONS =
			"spring.config.location="
					+ "classpath:application.yml,"
					+ "classpath:aws.yml";

	public static void main(String[] args) {
		new SpringApplicationBuilder(BeitoolApplication.class)
				.properties(APPLICATION_LOCATIONS)
				.run(args);
	}

}
