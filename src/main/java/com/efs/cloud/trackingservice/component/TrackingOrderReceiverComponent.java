package com.efs.cloud.trackingservice.component;

import com.alibaba.fastjson.JSON;
import com.efs.cloud.trackingservice.dto.TrackingActionInputDTO;
import com.efs.cloud.trackingservice.dto.TrackingOrderInputDTO;
import com.efs.cloud.trackingservice.entity.entity.OrderDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventActionEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventOrderEntity;
import com.efs.cloud.trackingservice.service.calculate.CalculateActionService;
import com.efs.cloud.trackingservice.service.calculate.CalculateCampaignService;
import com.efs.cloud.trackingservice.service.calculate.CalculateOrderService;
import com.efs.cloud.trackingservice.service.tracking.TrackingActionService;
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
 * @author jabez.huang
 */

@Component
@Slf4j
public class TrackingOrderReceiverComponent {

    @Autowired
    private TrackingOrderService trackingOrderService;
    @Autowired
    private CalculateOrderService calculateOrderService;
    @Autowired
    private CalculateCampaignService calculateCampaignService;

    @RabbitListener(queues = {"${output.rabbitmq.tracking.order.queue.name}"})
    public void getMessage(Channel channel, Message message){
        byte[] body = message.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String key = message.getMessageProperties().getReceivedRoutingKey();
            log.info("=============> order key:"+key);
            switch ( key ) {
                case "sync.order.tracking.order":
                    OrderDTOEntity orderDTOEntity = objectMapper.readValue(body, OrderDTOEntity.class);
                    Boolean isAck = trackingOrderService.receiveEventOrder( orderDTOEntity );
                    if( isAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.order.calculate.order_amount":
                    TrackingEventOrderEntity trackingEventOrderAmountEntity = JSON.parseObject(body, TrackingEventOrderEntity.class);
                    Boolean isCalAck = calculateOrderService.receiveCalculateOrderAmount( trackingEventOrderAmountEntity );
                    if( isCalAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.order.calculate.order_item":
                    TrackingEventOrderEntity trackingEventOrderItemEntity = JSON.parseObject(body, TrackingEventOrderEntity.class);
                    Boolean isCalSearchAck = calculateOrderService.receiveCalculateOrderItem( trackingEventOrderItemEntity );
                    if( isCalSearchAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.order.calculate.order_category":
                    TrackingEventOrderEntity trackingEventOrderCategoryEntity = JSON.parseObject(body, TrackingEventOrderEntity.class);
                    Boolean isCalShareAck = calculateOrderService.receiveCalculateOrderCategory( trackingEventOrderCategoryEntity );
                    if( isCalShareAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.order.calculate.campaign_order":
                    TrackingEventOrderEntity trackingEventOrderCampaignEntity = JSON.parseObject(body, TrackingEventOrderEntity.class);
                    Boolean isCalShareAckCampaign = calculateCampaignService.receiveCalculateCampaignOrder( trackingEventOrderCampaignEntity );
                    if( isCalShareAckCampaign ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                default:
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),  false);
                    log.warn( "error order route key:" + message.getMessageProperties().getReceivedRoutingKey() );
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
