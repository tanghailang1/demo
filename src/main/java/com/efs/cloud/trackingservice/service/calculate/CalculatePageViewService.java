package com.efs.cloud.trackingservice.service.calculate;

import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.entity.calculate.*;
import com.efs.cloud.trackingservice.entity.tracking.TrackingPageViewEntity;
import com.efs.cloud.trackingservice.repository.calculate.*;
import com.efs.cloud.trackingservice.repository.tracking.TrackingPageViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Page 计算组合Service
 * @author jabez.huang
 */

@Slf4j
@Service
public class CalculatePageViewService {

    @Autowired
    private TrackingPageViewRepository trackingPageViewRepository;
    @Autowired
    private CalculatePageViewRepository calculatePageViewRepository;
    @Autowired
    private CalculatePageSceneRepository calculatePageSceneRepository;
    @Autowired
    private CalculatePageActionRepository calculatePageActionRepository;
    @Autowired
    private CalculatePagePathRepository calculatePagePathRepository;
    @Autowired
    private CalculateLogRepository calculateLogRepository;

    /**
     * 统计PageView
     * @param trackingPageViewEntity
     * @return
     */
    public Boolean receiveCalculatePageView(TrackingPageViewEntity trackingPageViewEntity){
        //判断是否当天多条存在
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Integer customer = 0;
        if( trackingPageViewEntity.getCustomerId() > 1 ){
            List<TrackingPageViewEntity> trackingPageViewEntityListCustomer = trackingPageViewRepository.findByCreateDateAndMerchantIdAndStoreIdAndCustomerId(calendar.getTime(),
                    trackingPageViewEntity.getMerchantId(), trackingPageViewEntity.getStoreId(),trackingPageViewEntity.getCustomerId() );
            if( trackingPageViewEntityListCustomer.size() > 1 ){
                customer = 1;
            }
        }

        Date currentTime = calendar.getTime();
        Integer hour =  calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findUniqueId( trackingPageViewEntity );

        CalculatePageViewEntity calculatePageViewEntity = calculatePageViewRepository.findByDateAndHourAndMerchantIdAndStoreId( currentTime,
                hour, trackingPageViewEntity.getMerchantId(), trackingPageViewEntity.getStoreId() );
        if (calculatePageViewEntity != null) {
            CalculatePageViewEntity calculatePageViewEntityExists = CalculatePageViewEntity.builder()
                    .cId( calculatePageViewEntity.getCId() )
                    .date( calculatePageViewEntity.getDate() )
                    .hour( calculatePageViewEntity.getHour() )
                    .merchantId( calculatePageViewEntity.getMerchantId() )
                    .storeId( calculatePageViewEntity.getStoreId() )
                    .pvCount(calculatePageViewEntity.getPvCount() + 1)
                    .uvCount( calculatePageViewEntity.getUvCount() + union )
                    .customerCount( calculatePageViewEntity.getCustomerCount() + customer )
                    .build();
            CalculatePageViewEntity isSave = calculatePageViewRepository.saveAndFlush( calculatePageViewEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_page_view").content(JSONObject.toJSONString(trackingPageViewEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculatePageViewEntity calculatePageViewEntityNew = CalculatePageViewEntity.builder()
                    .date( currentTime )
                    .hour( hour )
                    .merchantId( trackingPageViewEntity.getMerchantId() )
                    .storeId( trackingPageViewEntity.getStoreId() )
                    .pvCount(1)
                    .uvCount(1)
                    .customerCount(customer)
                    .build();
            CalculatePageViewEntity isSave = calculatePageViewRepository.saveAndFlush( calculatePageViewEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_page_view").content(JSONObject.toJSONString(trackingPageViewEntity)).createTime( currentTime ).build()
                );
            }
        }

        return true;
    }

    /**
     * 统计不同scene渠道
     * @param trackingPageViewEntity
     * @return
     */
    public Boolean receiveCalculateScene(TrackingPageViewEntity trackingPageViewEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Integer union = findUniqueId( trackingPageViewEntity );
        Date currentTime = calendar.getTime();
        Integer hour =  calendar.get(Calendar.HOUR_OF_DAY);

        CalculatePageSceneEntity calculateMerchantSceneEntity = calculatePageSceneRepository.findByDateAndHourAndMerchantIdAndStoreIdAndScene(
                currentTime,
                hour,
                trackingPageViewEntity.getMerchantId(),
                trackingPageViewEntity.getStoreId(),
                trackingPageViewEntity.getScene()
        );

        if (calculateMerchantSceneEntity != null) {
            CalculatePageSceneEntity calculatePageSceneEntityExists = CalculatePageSceneEntity.builder()
                    .csId( calculateMerchantSceneEntity.getCsId() )
                    .date( calculateMerchantSceneEntity.getDate() )
                    .hour( calculateMerchantSceneEntity.getHour() )
                    .merchantId( calculateMerchantSceneEntity.getMerchantId() )
                    .storeId( calculateMerchantSceneEntity.getStoreId() )
                    .scene( calculateMerchantSceneEntity.getScene() )
                    .pvCount( calculateMerchantSceneEntity.getPvCount() + 1 )
                    .uvCount( calculateMerchantSceneEntity.getUvCount() + union )
                    .build();
            CalculatePageSceneEntity isSave = calculatePageSceneRepository.saveAndFlush( calculatePageSceneEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_page_scene").content(JSONObject.toJSONString(trackingPageViewEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculatePageSceneEntity calculatePageSceneEntityNew = CalculatePageSceneEntity.builder()
                    .date( currentTime )
                    .hour( hour )
                    .merchantId( trackingPageViewEntity.getMerchantId() )
                    .storeId( trackingPageViewEntity.getStoreId() )
                    .scene( trackingPageViewEntity.getScene() )
                    .pvCount( 1 )
                    .uvCount( 1 )
                    .build();
            CalculatePageSceneEntity isSave = calculatePageSceneRepository.saveAndFlush( calculatePageSceneEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_page_scene").content(JSONObject.toJSONString(trackingPageViewEntity)).createTime( currentTime ).build()
                );
            }
        }

        return true;
    }

    /**
     * 统计不同Action页面标题
     * @param trackingPageViewEntity
     * @return
     */
    public Boolean receiveCalculateAction(TrackingPageViewEntity trackingPageViewEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Integer union = findUniqueId( trackingPageViewEntity );
        Date currentTime = calendar.getTime();
        Integer hour =  calendar.get(Calendar.HOUR_OF_DAY);

        CalculatePageActionEntity calculatePageActionEntity = calculatePageActionRepository.findByDateAndHourAndMerchantIdAndStoreIdAndAction(
            currentTime,
            hour,
            trackingPageViewEntity.getMerchantId(),
            trackingPageViewEntity.getStoreId(),
            trackingPageViewEntity.getAction()
        );

        if (calculatePageActionEntity != null) {
            CalculatePageActionEntity calculatePageActionEntityExists = CalculatePageActionEntity.builder()
                    .caId( calculatePageActionEntity.getCaId() )
                    .date( calculatePageActionEntity.getDate() )
                    .hour( calculatePageActionEntity.getHour() )
                    .merchantId( calculatePageActionEntity.getMerchantId() )
                    .storeId( calculatePageActionEntity.getStoreId() )
                    .action( calculatePageActionEntity.getAction() )
                    .pvCount( calculatePageActionEntity.getPvCount() + 1 )
                    .uvCount( calculatePageActionEntity.getUvCount() + union )
                    .build();
            CalculatePageActionEntity isSave = calculatePageActionRepository.saveAndFlush( calculatePageActionEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_page_action").content(JSONObject.toJSONString(trackingPageViewEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculatePageActionEntity calculatePageActionEntityNew = CalculatePageActionEntity.builder()
                    .date( currentTime )
                    .hour( hour )
                    .merchantId( trackingPageViewEntity.getMerchantId() )
                    .storeId( trackingPageViewEntity.getStoreId() )
                    .action( trackingPageViewEntity.getAction() )
                    .uvCount( 1 )
                    .pvCount( 1 )
                    .build();
            CalculatePageActionEntity isSave = calculatePageActionRepository.saveAndFlush( calculatePageActionEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_page_action").content(JSONObject.toJSONString(trackingPageViewEntity)).createTime( currentTime ).build()
                );
            }
        }

        return true;
    }

    /**
     * 统计不同Path页面标题
     * @param trackingPageViewEntity
     * @return
     */
    public Boolean receiveCalculatePath(TrackingPageViewEntity trackingPageViewEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Integer union = findUniqueId( trackingPageViewEntity );
        Date currentTime = calendar.getTime();
        Integer hour =  calendar.get(Calendar.HOUR_OF_DAY);

        CalculatePagePathEntity calculateMerchantPathEntity = calculatePagePathRepository.findByDateAndHourAndMerchantIdAndStoreIdAndPath(
                currentTime,
                hour,
                trackingPageViewEntity.getMerchantId(),
                trackingPageViewEntity.getStoreId(),
                trackingPageViewEntity.getPath()
        );

        if (calculateMerchantPathEntity != null) {
            CalculatePagePathEntity calculatePagePathEntityExists = CalculatePagePathEntity.builder()
                    .cpId( calculateMerchantPathEntity.getCpId() )
                    .date( calculateMerchantPathEntity.getDate() )
                    .hour( calculateMerchantPathEntity.getHour() )
                    .merchantId( calculateMerchantPathEntity.getMerchantId() )
                    .storeId( calculateMerchantPathEntity.getStoreId() )
                    .path( calculateMerchantPathEntity.getPath() )
                    .pvCount( calculateMerchantPathEntity.getPvCount() + 1 )
                    .uvCount( calculateMerchantPathEntity.getUvCount() + union )
                    .build();
            CalculatePagePathEntity isSave = calculatePagePathRepository.saveAndFlush( calculatePagePathEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_page_path").content(JSONObject.toJSONString(trackingPageViewEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculatePagePathEntity calculateMerchantPathEntityNew = CalculatePagePathEntity.builder()
                    .date( currentTime )
                    .hour( hour )
                    .merchantId( trackingPageViewEntity.getMerchantId() )
                    .storeId( trackingPageViewEntity.getStoreId() )
                    .path( trackingPageViewEntity.getPath() )
                    .pvCount( 1 )
                    .uvCount( 1 )
                    .build();
            CalculatePagePathEntity isSave = calculatePagePathRepository.saveAndFlush( calculateMerchantPathEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_page_path").content(JSONObject.toJSONString(trackingPageViewEntity)).createTime( currentTime ).build()
                );
            }
        }

        return true;
    }

    private Integer findUniqueId(TrackingPageViewEntity trackingPageViewEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        List<TrackingPageViewEntity> trackingPageViewEntityList = trackingPageViewRepository.findByUniqueIdAndMerchantIdAndStoreIdAndCreateDate( trackingPageViewEntity.getUniqueId(),
                trackingPageViewEntity.getMerchantId(), trackingPageViewEntity.getStoreId(), calendar.getTime() );
        Integer union = 1;
        if( trackingPageViewEntityList.size() > 1 ){
            union = 0;
        }
        return union;
    }

}
