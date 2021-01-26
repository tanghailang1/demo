package com.efs.cloud.trackingservice.service.calculate;

import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.component.ElasticComponent;
import com.efs.cloud.trackingservice.entity.calculate.*;
import com.efs.cloud.trackingservice.entity.tracking.TrackingPageViewEntity;
import com.efs.cloud.trackingservice.repository.calculate.*;
import com.efs.cloud.trackingservice.repository.tracking.TrackingPageViewRepository;
import com.efs.cloud.trackingservice.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.efs.cloud.trackingservice.Global.TRACKING_PAGE_INDEX;

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
    @Autowired
    private ElasticsearchService elasticsearchService;
    /**
     * 统计PageView
     * @param trackingPageViewEntity
     * @return
     */
    public Boolean receiveCalculatePageView(TrackingPageViewEntity trackingPageViewEntity){
        Integer customer = 1;
        Date currentTime = trackingPageViewEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        if( trackingPageViewEntity.getCustomerId() > 0 ){
            ElasticComponent.SearchDocumentResponse trackingPageViewEntityCustomerSdr = elasticsearchService.findByIndexByCreateDateAndMerchantIdAndStoreIdAndCustomerId(TRACKING_PAGE_INDEX,currentTime,
                    trackingPageViewEntity.getMerchantId(), trackingPageViewEntity.getStoreId(),trackingPageViewEntity.getCustomerId(),"","" );
            if( trackingPageViewEntityCustomerSdr.getHits().getTotal() > 1 ){
                customer = 0;
            }
        }

        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findUniqueId( trackingPageViewEntity, currentTime,"","" );

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
                    .uvCount(union)
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
        Date currentTime = trackingPageViewEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findUniqueId( trackingPageViewEntity, currentTime,"scene",trackingPageViewEntity.getScene().toString() );

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
                    .uvCount( union )
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
        Date currentTime = trackingPageViewEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findUniqueId( trackingPageViewEntity, currentTime ,"action",trackingPageViewEntity.getAction());

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
                    .uvCount( union )
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
        Date currentTime = trackingPageViewEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findUniqueId( trackingPageViewEntity, currentTime ,"path",trackingPageViewEntity.getPath());

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
                    .uvCount( union )
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

    private Integer findUniqueId(TrackingPageViewEntity trackingPageViewEntity, Date date,String field,String fieldValue){
        ElasticComponent.SearchDocumentResponse trackingPageViewEntitySdr = elasticsearchService.findByIndexByUniqueIdAndMerchantIdAndStoreIdAndCreateDate( TRACKING_PAGE_INDEX,trackingPageViewEntity.getUniqueId(),
                trackingPageViewEntity.getMerchantId(), trackingPageViewEntity.getStoreId(), date ,field,fieldValue);
        Integer union = 1;
        if( trackingPageViewEntitySdr.getHits().getTotal() > 1 ){
            union = 0;
        }
        return union;
    }

}
