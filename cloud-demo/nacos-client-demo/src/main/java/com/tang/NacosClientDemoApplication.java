package com.tang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @Author: tang
 * @Date: 2022/8/15 15:02
 */
@RefreshScope
@EnableDiscoveryClient
@SpringBootApplication
public class NacosClientDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosClientDemoApplication.class, args);
    }

}
