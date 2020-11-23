package com.efs.cloud.trackingservice.service.tracking;

import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.component.TrackingSenderComponent;
import com.efs.cloud.trackingservice.dto.TrackingActionInputDTO;
import com.efs.cloud.trackingservice.dto.TrackingCartInputDTO;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventActionEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventCartEntity;
import com.efs.cloud.trackingservice.repository.tracking.TrackingEventActionRepository;
import com.efs.cloud.trackingservice.repository.tracking.TrackingEventCartRepository;
import com.efs.cloud.trackingservice.util.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author jabez.huang
 */
@Slf4j
@Service
public class TrackingCartService {

    @Value("${sync.calculate.cart_item}")
    private Boolean isTrackingCartItem;
    @Value("${sync.calculate.cart_sku}")
    private Boolean isTrackingCartSku;
    @Value("${sync.calculate.cart_campaign}")
    private boolean isCalculateCampaign;
    @Autowired
    private TrackingEventCartRepository trackingEventCartRepository;
    @Autowired
    private TrackingSenderComponent trackingSenderComponent;

    /**
     * 记录加购事件
     * @param trackingCartInputDTO
     * @return
     */
    public ServiceResult eventTrackingCart(TrackingCartInputDTO trackingCartInputDTO){
        String jsonObject = JSONObject.toJSONString( trackingCartInputDTO );
        log.info( "==============> pageTrackingCart:"+jsonObject );
        trackingSenderComponent.sendTracking( "sync.cart.tracking.cart", jsonObject );
        return ServiceResult.builder().code(200).data(null).msg("Success").build();
    }

    /**
     * 存储基础事件
     * @param trackingCartInputDTO
     * @return
     */
    public Boolean receiveEventCart(TrackingCartInputDTO trackingCartInputDTO) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);

        TrackingEventCartEntity trackingEventCartEntity = TrackingEventCartEntity.builder()
                .itemName( trackingCartInputDTO.getItemName() )
                .itemId( trackingCartInputDTO.getItemId() )
                .valueCode( trackingCartInputDTO.getValueCode() )
                .valueName( trackingCartInputDTO.getValueName() )
                .skuCode( trackingCartInputDTO.getSkuCode() )
                .itemPrice( trackingCartInputDTO.getItemPrice() )
                .scene( trackingCartInputDTO.getScene() )
                .campaign( trackingCartInputDTO.getCampaign() )
                .uniqueId( trackingCartInputDTO.getUniqueId() )
                .customerId( trackingCartInputDTO.getCustomerId() )
                .merchantId( trackingCartInputDTO.getMerchantId() )
                .storeId( trackingCartInputDTO.getStoreId() )
                .data( DataConvertUtil.objectConvertJson(trackingCartInputDTO.getData()) )
                .createDate( calendar.getTime() )
                .createTime( calendar.getTime() )
                .build();
        TrackingEventCartEntity trackingEventCartEntityNew = trackingEventCartRepository.saveAndFlush( trackingEventCartEntity );
        if( trackingEventCartEntityNew != null ){
            //cart item
            if( isTrackingCartItem ){
                trackingSenderComponent.sendTracking("sync.cart.calculate.cart_item", JSONObject.toJSONString( trackingEventCartEntityNew ));
            }

            //cart sku
            if( isTrackingCartSku ){
                trackingSenderComponent.sendTracking("sync.cart.calculate.cart_sku", JSONObject.toJSONString( trackingEventCartEntityNew ));
            }

            //campaign
            if( isCalculateCampaign && !"".equals(trackingCartInputDTO.getCampaign()) ){
                trackingSenderComponent.sendTracking( "sync.cart.calculate.campaign_cart", JSONObject.toJSONString( trackingCartInputDTO ) );
            }
            return true;
        }else{
            return false;
        }
    }
}
