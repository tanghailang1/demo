package com.efs.cloud.trackingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

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
