package com.efs.cloud.trackingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.TimeZone;
import java.util.concurrent.Executor;

/**
 * @author jabez.huang
 */
@SpringBootApplication
@EnableAsync
public class TrackingServiceApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		SpringApplication.run(TrackingServiceApplication.class, args);
	}

}
