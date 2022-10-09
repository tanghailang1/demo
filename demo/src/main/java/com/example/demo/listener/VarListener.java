package com.example.demo.listener;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.rabbitmq.MqQueueConstant;
import com.example.demo.service.impl.RabbitmqServiceImpl;
import com.example.demo.vo.SmsRecordInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * created by DengJin on 2019/12/16 17:32
 */
@Component
@Slf4j
public class VarListener {

    @Autowired
    private RabbitmqServiceImpl rabbitmqService;

    /**
     * 处理变量加工队列 监听
     */
    @RabbitListener(queues = MqQueueConstant.VAR_PROCESS_HANDLE, containerFactory = "rabbitListenerContainerFactory")
    public void processVarProcess(@Payload SmsRecordInfo smsRecordInfo) {
        try {
            log.info("接收到var_process_handle消息,{}", smsRecordInfo);
            System.out.println(smsRecordInfo);
            //往延时队列里put
            //rabbitmqService.sendOndDelay(MqQueueConstant.VAR_PROCESS_DELAY, smsRecordInfo, 60000);
        } catch (Exception e) {
            log.error("接收var_process_handle消息异常,requestVO={}", smsRecordInfo, e);
        }
    }


}
