package com.efs.cloud.trackingservice.service.calculate;

import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.entity.calculate.CalculateCartItemEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateCartSkuEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateLogEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventCartEntity;
import com.efs.cloud.trackingservice.repository.calculate.CalculateCartItemRepository;
import com.efs.cloud.trackingservice.repository.calculate.CalculateCartSkuRepository;
import com.efs.cloud.trackingservice.repository.calculate.CalculateLogRepository;
import com.efs.cloud.trackingservice.repository.tracking.TrackingEventCartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jabez.huang
 */

@Slf4j
@Service
public class CalculateCartService {

    @Autowired
    private TrackingEventCartRepository trackingEventCartRepository;
    @Autowired
    private CalculateCartItemRepository calculateCartItemRepository;
    @Autowired
    private CalculateCartSkuRepository calculateCartSkuRepository;
    @Autowired
    private CalculateLogRepository calculateLogRepository;

    /**
     * 统计购物车商品
     * @param trackingEventCartEntity
     * @return
     */
    public Boolean receiveCalculateCartItem(TrackingEventCartEntity trackingEventCartEntity){
        Date currentTime = trackingEventCartEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findCustomerId( trackingEventCartEntity, currentTime );

        CalculateCartItemEntity calculateCartItemEntity = calculateCartItemRepository.findByItemIdAndDateAndHourAndMerchantIdAndStoreId(
                trackingEventCartEntity.getItemId(), currentTime,
                hour, trackingEventCartEntity.getMerchantId(), trackingEventCartEntity.getStoreId() );

        if (calculateCartItemEntity != null) {
            CalculateCartItemEntity calculateCartItemEntityExists = CalculateCartItemEntity.builder()
                    .cartId( calculateCartItemEntity.getCartId() )
                    .itemId( calculateCartItemEntity.getItemId() )
                    .itemName( calculateCartItemEntity.getItemName() )
                    .date( calculateCartItemEntity.getDate() )
                    .hour( calculateCartItemEntity.getHour() )
                    .merchantId( calculateCartItemEntity.getMerchantId() )
                    .storeId( calculateCartItemEntity.getStoreId() )
                    .itemCartCount( calculateCartItemEntity.getItemCartCount() + 1)
                    .customerCount( calculateCartItemEntity.getCustomerCount() + union )
                    .build();
            CalculateCartItemEntity isSave = calculateCartItemRepository.saveAndFlush( calculateCartItemEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_cart_item").content(JSONObject.toJSONString(trackingEventCartEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculateCartItemEntity calculateCartItemEntityNew = CalculateCartItemEntity.builder()
                    .date( calendar.getTime() )
                    .hour( hour )
                    .itemId( trackingEventCartEntity.getItemId() )
                    .itemName( trackingEventCartEntity.getItemName() )
                    .merchantId( trackingEventCartEntity.getMerchantId() )
                    .storeId( trackingEventCartEntity.getStoreId() )
                    .itemCartCount( 1 )
                    .customerCount( 1 )
                    .build();
            CalculateCartItemEntity isSave = calculateCartItemRepository.saveAndFlush( calculateCartItemEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_cart_item").content(JSONObject.toJSONString(trackingEventCartEntity)).createTime( currentTime ).build()
                );
            }
        }

        return true;
    }

    /**
     * 统计购物车Sku
     * @param trackingEventCartEntity
     * @return
     */
    public Boolean receiveCalculateCartSku(TrackingEventCartEntity trackingEventCartEntity){
        Date currentTime = trackingEventCartEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findCustomerId( trackingEventCartEntity, currentTime );

        CalculateCartSkuEntity calculateCartSkuEntity = calculateCartSkuRepository.findBySkuCodeAndDateAndHourAndMerchantIdAndStoreId(
                trackingEventCartEntity.getSkuCode(), currentTime,
                hour, trackingEventCartEntity.getMerchantId(), trackingEventCartEntity.getStoreId() );

        if (calculateCartSkuEntity != null) {
            CalculateCartSkuEntity calculateCartSkuEntityExists = CalculateCartSkuEntity.builder()
                    .cartSkuId( calculateCartSkuEntity.getCartSkuId() )
                    .itemId( calculateCartSkuEntity.getItemId() )
                    .itemName( calculateCartSkuEntity.getItemName() )
                    .skuCode( calculateCartSkuEntity.getSkuCode())
                    .date( calculateCartSkuEntity.getDate() )
                    .hour( calculateCartSkuEntity.getHour() )
                    .merchantId( calculateCartSkuEntity.getMerchantId() )
                    .storeId( calculateCartSkuEntity.getStoreId() )
                    .skuCartCount( calculateCartSkuEntity.getSkuCartCount() + 1)
                    .customerCount( calculateCartSkuEntity.getCustomerCount() + union )
                    .build();
            CalculateCartSkuEntity isSave = calculateCartSkuRepository.saveAndFlush( calculateCartSkuEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_cart_sku").content(JSONObject.toJSONString(trackingEventCartEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculateCartSkuEntity calculateCartSkuEntityNew = CalculateCartSkuEntity.builder()
                    .itemId( trackingEventCartEntity.getItemId() )
                    .itemName( trackingEventCartEntity.getItemName() )
                    .skuCode( trackingEventCartEntity.getSkuCode())
                    .date( currentTime )
                    .hour( hour )
                    .merchantId( trackingEventCartEntity.getMerchantId() )
                    .storeId( trackingEventCartEntity.getStoreId() )
                    .skuCartCount( 1 )
                    .customerCount( 1 )
                    .build();
            CalculateCartSkuEntity isSave = calculateCartSkuRepository.saveAndFlush( calculateCartSkuEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_cart_sku").content(JSONObject.toJSONString(trackingEventCartEntity)).createTime( currentTime ).build()
                );
            }
        }

        return true;
    }

    private Integer findCustomerId(TrackingEventCartEntity trackingEventCartEntity, Date date){
        List<TrackingEventCartEntity> trackingEventCartEntityList = trackingEventCartRepository.findByCustomerIdAndMerchantIdAndStoreIdAndCreateDate( trackingEventCartEntity.getCustomerId(),
                trackingEventCartEntity.getMerchantId(), trackingEventCartEntity.getStoreId(), date );
        Integer union = 1;
        if( trackingEventCartEntityList.size() > 1 ){
            union = 0;
        }
        return union;
    }


}
