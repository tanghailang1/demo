package com.efs.cloud.trackingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * @author jabez.huang
 */
@SpringBootApplication
public class TrackingServiceApplication {

	public static void main(String[] args) {
		TimeZone.getTimeZone("GMT+8");
		SpringApplication.run(TrackingServiceApplication.class, args);
	}

}
