package com.efs.cloud.trackingservice.service.tracking;

import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.component.TrackingSenderComponent;
import com.efs.cloud.trackingservice.dto.TrackingPageInputDTO;
import com.efs.cloud.trackingservice.entity.tracking.TrackingPageViewEntity;
import com.efs.cloud.trackingservice.repository.tracking.TrackingPageViewRepository;
import com.efs.cloud.trackingservice.util.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 页面Tracking
 * @author jabez.huang
 */

@Slf4j
@Service
public class TrackingPageService {

    @Value("${sync.calculate.page}")
    private boolean isCalculatePageTracking;
    @Value("${sync.calculate.page_scene}")
    private boolean isCalculateSceneTracking;
    @Value("${sync.calculate.page_action}")
    private boolean isCalculateActionTracking;
    @Value("${sync.calculate.page_path}")
    private boolean isCalculatePathTracking;
    @Value("${sync.calculate.page_campaign}")
    private boolean isCalculateCampaign;

    @Autowired
    private TrackingSenderComponent trackingSenderComponent;
    @Autowired
    private TrackingPageViewRepository trackingPageViewRepository;

    /**
     * 跟踪记录页面
     * @param trackingPageInputDTO
     * @return
     */
    public ServiceResult pageTrackingView(TrackingPageInputDTO trackingPageInputDTO){
        String jsonObject = JSONObject.toJSONString( trackingPageInputDTO );
        trackingSenderComponent.sendTracking( "sync.page.tracking.page", jsonObject );
        return ServiceResult.builder().code(200).data(null).msg("Success").build();
    }

    /**
     * 获取全局唯一Id
     * @return
     */
    public ServiceResult pageGetUnionId(){
        return ServiceResult.builder().code(200).data(UUID.randomUUID().toString().replaceAll("-","")).msg("Success").build();
    }

    /**
     * 记录Tracking Page View 基础信息
     * @param trackingPageInputDTO
     * @return
     */
    public Boolean receivePageView(TrackingPageInputDTO trackingPageInputDTO){

        Calendar calendar = Calendar.getInstance(Locale.CHINA);

        TrackingPageViewEntity trackingPageViewEntity = TrackingPageViewEntity.builder()
                .action( trackingPageInputDTO.getTitle() )
                .uniqueId( trackingPageInputDTO.getUniqueId() )
                .customerId( trackingPageInputDTO.getCustomerId() )
                .scene( trackingPageInputDTO.getScene() )
                .path(trackingPageInputDTO.getPath())
                .ip( trackingPageInputDTO.getIp() )
                .campaign( trackingPageInputDTO.getCampaign() )
                .merchantId( trackingPageInputDTO.getMerchantId() )
                .storeId( trackingPageInputDTO.getStoreId() )
                .model( trackingPageInputDTO.getModel() )
                .size( trackingPageInputDTO.getSize() )
                .data( DataConvertUtil.objectConvertJson(trackingPageInputDTO.getData()) )
                .createTime( calendar.getTime() )
                .createDate( calendar.getTime() )
                .build();

        TrackingPageViewEntity trackingPageViewEntityNew = trackingPageViewRepository.saveAndFlush( trackingPageViewEntity );
        if( trackingPageViewEntityNew.getTId() != null ){
            //page view
            if( isCalculatePageTracking ){
                trackingSenderComponent.sendTracking( "sync.page.calculate.page", JSONObject.toJSONString( trackingPageViewEntityNew ) );
            }

            //scene
            if( isCalculateSceneTracking ){
                trackingSenderComponent.sendTracking( "sync.page.calculate.page_scene", JSONObject.toJSONString( trackingPageViewEntityNew ) );
            }

            //title
            if( isCalculateActionTracking ){
                trackingSenderComponent.sendTracking( "sync.page.calculate.page_action", JSONObject.toJSONString( trackingPageViewEntityNew ) );
            }

            //path
            if( isCalculatePathTracking ){
                trackingSenderComponent.sendTracking( "sync.page.calculate.page_path", JSONObject.toJSONString( trackingPageViewEntityNew ) );
            }

            //campaign
            if( isCalculateCampaign && !"".equals(trackingPageInputDTO.getCampaign()) ){
                trackingSenderComponent.sendTracking( "sync.page.calculate.campaign_page", JSONObject.toJSONString( trackingPageViewEntityNew ) );
            }

            return true;
        }else{
            return false;
        }
    }

}
