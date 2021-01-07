package com.efs.cloud.trackingservice.component;

import com.alibaba.fastjson.JSON;
import com.efs.cloud.trackingservice.dto.TrackingActionInputDTO;
import com.efs.cloud.trackingservice.dto.TrackingPageInputDTO;
import com.efs.cloud.trackingservice.entity.entity.ActionDTOEntity;
import com.efs.cloud.trackingservice.entity.entity.OrderDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventActionEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingPageViewEntity;
import com.efs.cloud.trackingservice.service.calculate.CalculateActionService;
import com.efs.cloud.trackingservice.service.calculate.CalculatePageViewService;
import com.efs.cloud.trackingservice.service.tracking.TrackingActionService;
import com.efs.cloud.trackingservice.service.tracking.TrackingPageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author jabez.huang
 */

@Component
@Slf4j
public class TrackingActionReceiverComponent {

    @Autowired
    private TrackingActionService trackingActionService;

    @Autowired
    private CalculateActionService calculateActionService;

    @RabbitListener(queues = {"${output.rabbitmq.tracking.action.queue.name}"})
    public void getMessage(Channel channel, Message message){
        byte[] body = message.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String key = message.getMessageProperties().getReceivedRoutingKey();
            log.info("=============> action key:"+key);
            switch ( key ) {
                case "sync.action.tracking.action":
                    ActionDTOEntity actionDTOEntity  = objectMapper.readValue(body, ActionDTOEntity.class);
                    Boolean isAck = trackingActionService.receiveEventAction( actionDTOEntity );
                    if( isAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.action.calculate.action":
                    TrackingEventActionEntity trackingActionEntity = JSON.parseObject(body, TrackingEventActionEntity.class);
                    Boolean isCalAck = calculateActionService.receiveCalculateAction( trackingActionEntity );
                    if( isCalAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.action.calculate.action_search":
                    TrackingEventActionEntity trackingActionSearchEntity = JSON.parseObject(body, TrackingEventActionEntity.class);
                    Boolean isCalSearchAck = calculateActionService.receiveCalculateActionSearch( trackingActionSearchEntity );
                    if( isCalSearchAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.action.calculate.action_cms":
                    TrackingEventActionEntity trackingActionCmsEntity = JSON.parseObject(body, TrackingEventActionEntity.class);
                    Boolean isCalCmsAck = calculateActionService.receiveCalculateActionCms( trackingActionCmsEntity );
                    if( isCalCmsAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.action.calculate.action_pdp_item":
                    TrackingEventActionEntity trackingActionPdpItemEntity = JSON.parseObject(body, TrackingEventActionEntity.class);
                    Boolean isCalPdpItemAck = calculateActionService.receiveCalculateActionPdpItem( trackingActionPdpItemEntity );
                    if( isCalPdpItemAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.action.calculate.action_share":
                    TrackingEventActionEntity trackingActionShareEntity = JSON.parseObject(body, TrackingEventActionEntity.class);
                    Boolean isCalShareAck = calculateActionService.receiveCalculateActionShare( trackingActionShareEntity );
                    if( isCalShareAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                default:
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    log.warn( "error action route key:" + message.getMessageProperties().getReceivedRoutingKey() );
                    break;
            }
        }catch (Exception e){
            log.info( "error order:" + e );
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),  false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
