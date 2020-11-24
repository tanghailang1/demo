package com.efs.cloud.trackingservice.component;

import com.alibaba.fastjson.JSON;
import com.efs.cloud.trackingservice.dto.TrackingActionInputDTO;
import com.efs.cloud.trackingservice.dto.TrackingCartInputDTO;
import com.efs.cloud.trackingservice.entity.entity.CartDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventActionEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventCartEntity;
import com.efs.cloud.trackingservice.service.calculate.CalculateActionService;
import com.efs.cloud.trackingservice.service.calculate.CalculateCampaignService;
import com.efs.cloud.trackingservice.service.calculate.CalculateCartService;
import com.efs.cloud.trackingservice.service.tracking.TrackingActionService;
import com.efs.cloud.trackingservice.service.tracking.TrackingCartService;
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
public class TrackingCartReceiverComponent {

    @Autowired
    private TrackingCartService trackingCartService;
    @Autowired
    private CalculateCartService calculateCartService;
    @Autowired
    private CalculateCampaignService calculateCampaignService;

    @RabbitListener(queues = {"${output.rabbitmq.tracking.cart.queue.name}"})
    public void getMessage(Channel channel, Message message){
        byte[] body = message.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String key = message.getMessageProperties().getReceivedRoutingKey();
            log.info("=============> cart key:"+key);
            switch ( key ) {
                case "sync.cart.tracking.cart":
                    CartDTOEntity cartDTOEntity = objectMapper.readValue(body, CartDTOEntity.class);
                    Boolean isAck = trackingCartService.receiveEventCart( cartDTOEntity );
                    if( isAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.cart.calculate.cart_item":
                    TrackingEventCartEntity trackingEventCartEntity = JSON.parseObject(body, TrackingEventCartEntity.class);
                    Boolean isCalAck = calculateCartService.receiveCalculateCartItem( trackingEventCartEntity );
                    if( isCalAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.cart.calculate.cart_sku":
                    TrackingEventCartEntity trackingCartSkuEntity = JSON.parseObject(body, TrackingEventCartEntity.class);
                    Boolean isCalSearchAck = calculateCartService.receiveCalculateCartSku( trackingCartSkuEntity );
                    if( isCalSearchAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.cart.calculate.campaign_cart":
                    TrackingEventCartEntity trackingEventCartCampaignEntity = JSON.parseObject(body, TrackingEventCartEntity.class);
                    Boolean isCalSearchAckCampaign = calculateCampaignService.receiveCalculateCampaignCart( trackingEventCartCampaignEntity );
                    if( isCalSearchAckCampaign ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                default:
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    log.warn( "error cart route key:" + message.getMessageProperties().getReceivedRoutingKey() );
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
