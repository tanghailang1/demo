package com.efs.cloud.trackingservice.service.tracking;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.component.TrackingSenderComponent;
import com.efs.cloud.trackingservice.dto.TrackingOrderInputDTO;
import com.efs.cloud.trackingservice.entity.entity.OrderDTOEntity;
import com.efs.cloud.trackingservice.entity.entity.OrderItemDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventOrderEntity;
import com.efs.cloud.trackingservice.enums.OrderStatusEnum;
import com.efs.cloud.trackingservice.repository.tracking.TrackingEventOrderRepository;
import com.efs.cloud.trackingservice.util.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author jabez.huang
 */
@Slf4j
@Service
public class TrackingOrderService {

    @Value("${sync.calculate.order_amount}")
    private Boolean isTrackingOrderAmount;
    @Value("${sync.calculate.order_item}")
    private Boolean isTrackingOrderItem;
    @Value("${sync.calculate.order_category}")
    private Boolean isTrackingOrderCategory;
    @Value("${sync.calculate.order_campaign}")
    private boolean isCalculateCampaign;
    @Autowired
    private TrackingEventOrderRepository trackingEventOrderRepository;
    @Autowired
    private TrackingSenderComponent trackingSenderComponent;


    /**
     * 记录加购事件
     * @param trackingOrderInputDTO
     * @return
     */
    public ServiceResult eventTrackingOrder(TrackingOrderInputDTO trackingOrderInputDTO, OrderStatusEnum orderStatusEnum){
        String jsonObject = JSONObject.toJSONString( OrderDTOEntity.builder().orderStatus(orderStatusEnum.getValue()).trackingOrderInputDTO(trackingOrderInputDTO).build() );
        trackingSenderComponent.sendTracking( "sync.order.tracking.order", jsonObject );
        return ServiceResult.builder().code(200).data(null).msg("Success").build();
    }

    /**
     * 存储基础事件
     * @param orderDTOEntity
     * @return
     */
    public Boolean receiveEventOrder(OrderDTOEntity orderDTOEntity) {
        TrackingOrderInputDTO trackingOrderInputDTO = orderDTOEntity.getTrackingOrderInputDTO();
        String status = orderDTOEntity.getOrderStatus();

        Calendar calendar = Calendar.getInstance(Locale.CHINA);

        String receiveOther = "";
        if( !"".equals(trackingOrderInputDTO.getOrderItems().toString()) ){
            List<OrderItemDTOEntity> list = trackingOrderInputDTO.getOrderItems();
            JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(list));
            receiveOther = jsonArray.toJSONString();
        }

        TrackingEventOrderEntity trackingEventOrderEntity = TrackingEventOrderEntity.builder()
                .orderAmount( trackingOrderInputDTO.getOrderAmount() )
                .orderShippingFee( trackingOrderInputDTO.getOrderShippingFee() )
                .orderDiscountAmount( trackingOrderInputDTO.getOrderDiscountAmount() )
                .orderItems( receiveOther )
                .orderSubtotal( trackingOrderInputDTO.getOrderSubtotal() )
                .scene( trackingOrderInputDTO.getScene() )
                .ip( trackingOrderInputDTO.getIp() )
                .status( status )
                .campaign( trackingOrderInputDTO.getCampaign() )
                .uniqueId( trackingOrderInputDTO.getUniqueId() )
                .orderId( trackingOrderInputDTO.getOrderId() )
                .customerId( trackingOrderInputDTO.getCustomerId() )
                .merchantId( trackingOrderInputDTO.getMerchantId() )
                .storeId( trackingOrderInputDTO.getStoreId() )
                .data( DataConvertUtil.objectConvertJson(trackingOrderInputDTO.getData()) )
                .createDate( calendar.getTime() )
                .createTime( calendar.getTime() )
                .build();
        TrackingEventOrderEntity trackingEventOrderEntityNew = trackingEventOrderRepository.saveAndFlush( trackingEventOrderEntity );
        if( trackingEventOrderEntityNew != null ){
            //order amount
            if( isTrackingOrderAmount ){
                trackingSenderComponent.sendTracking("sync.order.calculate.order_amount", JSONObject.toJSONString( trackingEventOrderEntityNew ));
            }

            //order item
            if( isTrackingOrderItem ){
                trackingSenderComponent.sendTracking("sync.order.calculate.order_item", JSONObject.toJSONString( trackingEventOrderEntityNew ));
            }

            //order category
            if( isTrackingOrderCategory ){
                trackingSenderComponent.sendTracking("sync.order.calculate.order_category", JSONObject.toJSONString( trackingEventOrderEntityNew ));
            }

            //campaign
            if( isCalculateCampaign && !"".equals(trackingOrderInputDTO.getCampaign()) ){
                trackingSenderComponent.sendTracking( "sync.order.calculate.campaign_order", JSONObject.toJSONString( trackingEventOrderEntityNew ) );
            }
            return true;
        }else{
            return false;
        }
    }
}
