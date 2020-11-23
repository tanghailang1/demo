package com.efs.cloud.trackingservice.component;

import com.alibaba.fastjson.JSON;
import com.efs.cloud.trackingservice.dto.TrackingPageInputDTO;
import com.efs.cloud.trackingservice.entity.tracking.TrackingPageViewEntity;
import com.efs.cloud.trackingservice.service.calculate.CalculateCampaignService;
import com.efs.cloud.trackingservice.service.calculate.CalculatePageViewService;
import com.efs.cloud.trackingservice.service.tracking.TrackingPageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jabez.huang
 */

@Component
@Slf4j
public class TrackingPageReceiverComponent {

    @Autowired
    private TrackingPageService trackingPageService;
    @Autowired
    private CalculatePageViewService calculatePageViewService;
    @Autowired
    private CalculateCampaignService calculateCampaignService;

    @RabbitListener(queues = {"${output.rabbitmq.tracking.page.queue.name}"})
    public void getMessage(Channel channel, Message message){
        byte[] body = message.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String key = message.getMessageProperties().getReceivedRoutingKey();
            log.info("=============> page key:"+key);
            switch ( key ) {
                case "sync.page.tracking.page":
                    TrackingPageInputDTO trackingPageInputDTO = objectMapper.readValue(body, TrackingPageInputDTO.class);
                    Boolean isAck = trackingPageService.receivePageView( trackingPageInputDTO );
                    if( isAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.page.calculate.page":
                    TrackingPageViewEntity trackingPageViewEntity = JSON.parseObject(body, TrackingPageViewEntity.class);
                    Boolean isCalAck = calculatePageViewService.receiveCalculatePageView( trackingPageViewEntity );
                    if( isCalAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.page.calculate.page_scene":
                    TrackingPageViewEntity trackingPageViewEntityScene = JSON.parseObject(body, TrackingPageViewEntity.class);
                    Boolean isCalSceneAck = calculatePageViewService.receiveCalculateScene( trackingPageViewEntityScene );
                    if( isCalSceneAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.page.calculate.page_action":
                    TrackingPageViewEntity trackingPageViewEntityAction = JSON.parseObject(body, TrackingPageViewEntity.class);
                    Boolean isCalActionAck = calculatePageViewService.receiveCalculateAction( trackingPageViewEntityAction );
                    if( isCalActionAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.page.calculate.page_path":
                    TrackingPageViewEntity trackingPageViewEntityPath = JSON.parseObject(body, TrackingPageViewEntity.class);
                    Boolean isCalPathAck = calculatePageViewService.receiveCalculatePath( trackingPageViewEntityPath );
                    if( isCalPathAck ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                case "sync.page.calculate.campaign_page":
                    TrackingPageViewEntity trackingPageViewEntityCampaign = JSON.parseObject(body, TrackingPageViewEntity.class);
                    Boolean isCalAckCampaign = calculateCampaignService.receiveCalculateCampaignPage( trackingPageViewEntityCampaign );
                    if( isCalAckCampaign ){
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    }else{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                    break;
                default:
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
                    log.warn( "error page route key:" + message.getMessageProperties().getReceivedRoutingKey() );
                    break;
            }
        }catch (Exception e){
            log.info( "error page:" + e );
        }
    }
}
