package com.example.demo.service.impl;

import com.example.demo.service.MqService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitmqServiceImpl implements MqService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到队列
     * @param queue
     * @param msg
     * @return
     */
    @Override
    public boolean sendOne(String queue, Object msg) {
        this.rabbitTemplate.convertAndSend(queue, msg);
        return true;
    }

    /**
     * 发送消息到延迟队列
     * @param queue
     * @param msg
     * @param ttl
     * @return
     */
    public boolean sendOndDelay(String queue, Object msg, Integer ttl){
        rabbitTemplate.convertAndSend(queue, msg, (message) -> {
            message.getMessageProperties().setExpiration(String.valueOf(ttl));
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
        return true;
    }

    @Override
    public int getMsgNumber(String queue){
        AMQP.Queue.DeclareOk declareOk = rabbitTemplate.execute(channel -> channel.queueDeclarePassive(queue));
        assert declareOk != null;
        return declareOk.getMessageCount();
    }
}
