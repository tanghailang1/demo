package com.efs.cloud.trackingservice.component;

import com.alibaba.fastjson.JSON;
import com.efs.cloud.trackingservice.entity.entity.LogDTOEntity;
import com.efs.cloud.trackingservice.entity.entity.OrderDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventOrderEntity;
import com.efs.cloud.trackingservice.service.calculate.CalculateCampaignService;
import com.efs.cloud.trackingservice.service.calculate.CalculateOrderService;
import com.efs.cloud.trackingservice.service.tracking.TrackingLogService;
import com.efs.cloud.trackingservice.service.tracking.TrackingOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author maxun
 */

@Component
@Slf4j
public class TrackingLogReceiverComponent {

    @Autowired
    private TrackingLogService trackingLogService;

    @RabbitListener(queues = {"${output.rabbitmq.tracking.log.queue.name}"})
    public void getMessage(Channel channel, Message message){
        byte[] body = message.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String key = message.getMessageProperties().getReceivedRoutingKey();
            log.info("=============> log key:"+key);
            switch ( key ) {
                case "sync.log.tracking":
                    LogDTOEntity logDTOEntity = objectMapper.readValue(body, LogDTOEntity.class);
                    Boolean isAck = trackingLogService.receiveEventOrder( logDTOEntity );
                    if( isAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                default:
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),  false);
                    log.warn( "error log route key:" + message.getMessageProperties().getReceivedRoutingKey() );
                    break;
            }
        }catch (Exception e){
            log.info( "error log:" + e );
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),  false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
