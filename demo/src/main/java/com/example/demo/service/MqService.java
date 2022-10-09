package com.example.demo.service;

import com.rabbitmq.client.AMQP;

/**
 * created by DengJin on 2019/12/16 15:18
 */
public interface MqService {

    boolean sendOne(String topic, Object message);

    boolean sendOndDelay(String queue, Object msg, Integer ttl);

    int getMsgNumber(String queue);
}
