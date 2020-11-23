package com.efs.cloud.trackingservice.service.calculate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.entity.calculate.CalculateCampaignEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateLogEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventCartEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventOrderEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingPageViewEntity;
import com.efs.cloud.trackingservice.enums.OrderStatusEnum;
import com.efs.cloud.trackingservice.repository.calculate.CalculateCampaignRepository;
import com.efs.cloud.trackingservice.repository.calculate.CalculateLogRepository;
import com.efs.cloud.trackingservice.repository.tracking.TrackingPageViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author jabez.huang
 */

@Slf4j
@Service
public class CalculateCampaignService {

    @Autowired
    private TrackingPageViewRepository trackingPageViewRepository;
    @Autowired
    private CalculateCampaignRepository calculateCampaignRepository;
    @Autowired
    private CalculateLogRepository calculateLogRepository;

    /**
     * Campaign 渠道计算流量
     * @param trackingPageViewEntity
     * @return
     */
    public boolean receiveCalculateCampaignPage(TrackingPageViewEntity trackingPageViewEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        List<TrackingPageViewEntity> trackingPageViewEntityListCustomer = trackingPageViewRepository.findByCreateDateAndMerchantIdAndStoreIdAndCustomerId(calendar.getTime(),
                trackingPageViewEntity.getMerchantId(), trackingPageViewEntity.getStoreId(),trackingPageViewEntity.getCustomerId() );

        Integer customer = 1;
        List<TrackingPageViewEntity> trackingPageViewEntityList = trackingPageViewRepository.findByUniqueIdAndMerchantIdAndStoreIdAndCreateDate( trackingPageViewEntity.getUniqueId(),
                trackingPageViewEntity.getMerchantId(), trackingPageViewEntity.getStoreId(), calendar.getTime() );
        Integer union = 1;
        if( trackingPageViewEntityList.size() > 1 ){
            union = 0;
        }

        if( trackingPageViewEntityListCustomer.size() > 1 ){
            customer = 0;
        }

        Date currentTime = calendar.getTime();
        Integer hour =  calendar.get(Calendar.HOUR_OF_DAY);

        CalculateCampaignEntity calculateCampaignEntity = calculateCampaignRepository.findByCampaignNameAndCreateDateAndHourAndMerchantIdAndStoreId(
                trackingPageViewEntity.getCampaign(),
                currentTime,
                hour,
                trackingPageViewEntity.getMerchantId(),
                trackingPageViewEntity.getStoreId()
        );
        if( calculateCampaignEntity != null ){
            CalculateCampaignEntity calculateCampaignEntityExists = CalculateCampaignEntity.builder()
                    .campaignCalculateId( calculateCampaignEntity.getCampaignCalculateId() )
                    .campaignName( calculateCampaignEntity.getCampaignName() )
                    .pvCount( calculateCampaignEntity.getPvCount() + 1 )
                    .uvCount( calculateCampaignEntity.getUvCount() + union )
                    .customerCount( calculateCampaignEntity.getCustomerCount() + customer )
                    .cartCount( calculateCampaignEntity.getCartCount() )
                    .createOrderCount( calculateCampaignEntity.getCreateOrderCount() )
                    .createOrderAmount( calculateCampaignEntity.getCreateOrderAmount() )
                    .orderCount( calculateCampaignEntity.getOrderCount() )
                    .orderAmount( calculateCampaignEntity.getOrderAmount() )
                    .createDate( calculateCampaignEntity.getCreateDate() )
                    .hour( calculateCampaignEntity.getHour() )
                    .merchantId( calculateCampaignEntity.getMerchantId() )
                    .storeId( calculateCampaignEntity.getStoreId() )
                    .build();
            CalculateCampaignEntity isSave = calculateCampaignRepository.saveAndFlush( calculateCampaignEntityExists );
            if( isSave == null  ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_campaign").content(JSONObject.toJSONString(trackingPageViewEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculateCampaignEntity calculateCampaignEntityNew = CalculateCampaignEntity.builder()
                    .campaignName( trackingPageViewEntity.getCampaign() )
                    .pvCount( 1 )
                    .uvCount( 1 )
                    .customerCount( customer )
                    .cartCount( 0 )
                    .createOrderCount( 0 )
                    .createOrderAmount( 0 )
                    .orderCount( 0 )
                    .orderAmount( 0 )
                    .createDate( currentTime )
                    .hour( hour )
                    .merchantId( trackingPageViewEntity.getMerchantId() )
                    .storeId( trackingPageViewEntity.getStoreId() )
                    .build();
            CalculateCampaignEntity isSave = calculateCampaignRepository.saveAndFlush( calculateCampaignEntityNew );
            if( isSave == null  ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_campaign").content(JSONObject.toJSONString(trackingPageViewEntity)).createTime( currentTime ).build()
                );
            }
        }

        return true;
    }

    /**
     * Campaign渠道购物车
     * @param trackingEventCartEntity
     * @return
     */
    public boolean receiveCalculateCampaignCart(TrackingEventCartEntity trackingEventCartEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date currentTime = calendar.getTime();
        Integer hour =  calendar.get(Calendar.HOUR_OF_DAY);

        CalculateCampaignEntity calculateCampaignEntity = calculateCampaignRepository.findByCampaignNameAndCreateDateAndHourAndMerchantIdAndStoreId(
                trackingEventCartEntity.getCampaign(),
                currentTime,
                hour,
                trackingEventCartEntity.getMerchantId(),
                trackingEventCartEntity.getStoreId()
        );
        if( calculateCampaignEntity != null ){
            CalculateCampaignEntity calculateCampaignEntityExists = CalculateCampaignEntity.builder()
                    .campaignCalculateId( calculateCampaignEntity.getCampaignCalculateId() )
                    .campaignName( calculateCampaignEntity.getCampaignName() )
                    .pvCount( calculateCampaignEntity.getPvCount() )
                    .uvCount( calculateCampaignEntity.getUvCount() )
                    .customerCount( calculateCampaignEntity.getCustomerCount() )
                    .cartCount( calculateCampaignEntity.getCartCount() + 1 )
                    .createOrderCount( calculateCampaignEntity.getCreateOrderCount() )
                    .createOrderAmount( calculateCampaignEntity.getCreateOrderAmount() )
                    .orderCount( calculateCampaignEntity.getOrderCount() )
                    .orderAmount( calculateCampaignEntity.getOrderAmount() )
                    .createDate( calculateCampaignEntity.getCreateDate() )
                    .hour( calculateCampaignEntity.getHour() )
                    .merchantId( calculateCampaignEntity.getMerchantId() )
                    .storeId( calculateCampaignEntity.getStoreId() )
                    .build();
            CalculateCampaignEntity isSave = calculateCampaignRepository.saveAndFlush( calculateCampaignEntityExists );
            if( isSave == null  ){
                return false;
            }
        }else{
            CalculateCampaignEntity calculateCampaignEntityNew = CalculateCampaignEntity.builder()
                    .campaignName( trackingEventCartEntity.getCampaign() )
                    .pvCount( 1 )
                    .uvCount( 1 )
                    .customerCount( 1 )
                    .cartCount( 1 )
                    .orderCount( 0 )
                    .orderAmount( 0 )
                    .createDate( currentTime )
                    .hour( hour )
                    .merchantId( trackingEventCartEntity.getMerchantId() )
                    .storeId( trackingEventCartEntity.getStoreId() )
                    .build();
            CalculateCampaignEntity isSave = calculateCampaignRepository.saveAndFlush( calculateCampaignEntityNew );
            if( isSave == null  ){
                return false;
            }
        }

        return true;
    }

    /**
     * Campaign渠道Order计算
     * @param trackingEventOrderEntity
     * @return
     */
    public boolean receiveCalculateCampaignOrder(TrackingEventOrderEntity trackingEventOrderEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date currentTime = calendar.getTime();
        Integer hour =  calendar.get(Calendar.HOUR_OF_DAY);

        CalculateCampaignEntity calculateCampaignEntity = calculateCampaignRepository.findByCampaignNameAndCreateDateAndHourAndMerchantIdAndStoreId(
                trackingEventOrderEntity.getCampaign(),
                currentTime,
                hour,
                trackingEventOrderEntity.getMerchantId(),
                trackingEventOrderEntity.getStoreId()
        );
        Integer createOrderCount = 0;
        Integer createOrderAmount = 0;
        Integer orderCount = 0;
        Integer orderAmount = 0;
        if( OrderStatusEnum.ORDER_PAY.getValue().equals(trackingEventOrderEntity.getStatus())){
            createOrderCount = 1;
            createOrderAmount = trackingEventOrderEntity.getOrderAmount();
        }else{
            orderCount = 1;
            orderAmount = trackingEventOrderEntity.getOrderAmount();
        }
        if( calculateCampaignEntity != null ){
            CalculateCampaignEntity calculateCampaignEntityExists = CalculateCampaignEntity.builder()
                    .campaignCalculateId( calculateCampaignEntity.getCampaignCalculateId() )
                    .campaignName( calculateCampaignEntity.getCampaignName() )
                    .pvCount( calculateCampaignEntity.getPvCount() )
                    .uvCount( calculateCampaignEntity.getUvCount() )
                    .customerCount( calculateCampaignEntity.getCustomerCount() )
                    .cartCount( calculateCampaignEntity.getCartCount() )
                    .createOrderCount( calculateCampaignEntity.getCreateOrderCount() + createOrderCount )
                    .createOrderAmount( calculateCampaignEntity.getCreateOrderAmount() + createOrderAmount )
                    .orderCount( calculateCampaignEntity.getOrderCount() + orderCount )
                    .orderAmount( calculateCampaignEntity.getOrderAmount() + orderAmount )
                    .createDate( calculateCampaignEntity.getCreateDate() )
                    .hour( calculateCampaignEntity.getHour() )
                    .merchantId( calculateCampaignEntity.getMerchantId() )
                    .storeId( calculateCampaignEntity.getStoreId() )
                    .build();
            CalculateCampaignEntity isSave = calculateCampaignRepository.saveAndFlush( calculateCampaignEntityExists );
            if( isSave == null  ){
                return false;
            }
        }else{
            CalculateCampaignEntity calculateCampaignEntityNew = CalculateCampaignEntity.builder()
                    .campaignName( trackingEventOrderEntity.getCampaign() )
                    .pvCount( 1 )
                    .uvCount( 1 )
                    .customerCount( 1 )
                    .cartCount( 0 )
                    .createOrderCount( createOrderCount )
                    .createOrderAmount( createOrderAmount )
                    .orderCount( orderCount )
                    .orderAmount( orderAmount )
                    .createDate( currentTime )
                    .hour( hour )
                    .merchantId( trackingEventOrderEntity.getMerchantId() )
                    .storeId( trackingEventOrderEntity.getStoreId() )
                    .build();
            CalculateCampaignEntity isSave = calculateCampaignRepository.saveAndFlush( calculateCampaignEntityNew );
            if( isSave == null  ){
                return false;
            }
        }

        return true;
    }

}
