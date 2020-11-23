package com.efs.cloud.trackingservice.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author jabez.huang
 */
@Component
@Slf4j
public class TrackingSenderComponent {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${output.rabbitmq.tracking.exchange.name}")
    private String trackingExchange;

    public void sendTracking(String routeKey, String message){
        log.info("====> route Key : " +  routeKey );
        this.amqpTemplate.convertAndSend(trackingExchange, routeKey, message);
    }
}
