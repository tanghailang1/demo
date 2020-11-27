package com.efs.cloud.trackingservice.service.calculate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.component.ElasticComponent;
import com.efs.cloud.trackingservice.entity.calculate.CalculateCampaignEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateLogEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventCartEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventOrderEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingPageViewEntity;
import com.efs.cloud.trackingservice.enums.OrderStatusEnum;
import com.efs.cloud.trackingservice.repository.calculate.CalculateCampaignRepository;
import com.efs.cloud.trackingservice.repository.calculate.CalculateLogRepository;
import com.efs.cloud.trackingservice.repository.tracking.TrackingPageViewRepository;
import com.efs.cloud.trackingservice.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.efs.cloud.trackingservice.Global.TRACKING_PAGE_INDEX;

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
    @Autowired
    private ElasticsearchService elasticsearchService;

    /**
     * Campaign 渠道计算流量
     * @param trackingPageViewEntity
     * @return
     */
    public boolean receiveCalculateCampaignPage(TrackingPageViewEntity trackingPageViewEntity){
        Date currentTime = trackingPageViewEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);

        ElasticComponent.SearchDocumentResponse trackingPageViewEntityCustomerSdr = elasticsearchService.findByIndexByCreateDateAndMerchantIdAndStoreIdAndCustomerId(TRACKING_PAGE_INDEX,currentTime,
                trackingPageViewEntity.getMerchantId(), trackingPageViewEntity.getStoreId(),trackingPageViewEntity.getCustomerId() );

        Integer customer = 1;
        ElasticComponent.SearchDocumentResponse trackingPageViewEntityUnionSdr = elasticsearchService.findByIndexByUniqueIdAndMerchantIdAndStoreIdAndCreateDate( TRACKING_PAGE_INDEX,trackingPageViewEntity.getUniqueId(),
                trackingPageViewEntity.getMerchantId(), trackingPageViewEntity.getStoreId(), currentTime );
        Integer union = 1;
        if( trackingPageViewEntityUnionSdr.getHits().getTotal() > 1 ){
            union = 0;
        }

        if( trackingPageViewEntityCustomerSdr.getHits().getTotal() > 1 ){
            customer = 0;
        }

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
        Date currentTime = trackingEventCartEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);

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
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_campaign_cart").content(JSONObject.toJSONString(trackingEventCartEntity)).createTime( currentTime ).build()
                );
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
                calculateLogRepository.saveAndFlush(
                        CalculateLogEntity.builder().type("calculate_campaign_cart").content(JSONObject.toJSONString(trackingEventCartEntity)).createTime( currentTime ).build()
                );
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
        Date currentTime = trackingEventOrderEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);

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
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_campaign_order").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                );
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
            calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_campaign_order").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
            );
        }

        return true;
    }

}
