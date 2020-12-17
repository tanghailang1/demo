package com.efs.cloud.trackingservice.service.tracking;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.component.ElasticComponent;
import com.efs.cloud.trackingservice.component.TrackingSenderComponent;
import com.efs.cloud.trackingservice.dto.TrackingPageInputDTO;
import com.efs.cloud.trackingservice.entity.entity.PageViewDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingPageViewEntity;
import com.efs.cloud.trackingservice.repository.tracking.TrackingPageViewRepository;
import com.efs.cloud.trackingservice.service.JwtService;
import com.efs.cloud.trackingservice.util.DataConvertUtil;
import com.efs.cloud.trackingservice.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

import static com.efs.cloud.trackingservice.Global.*;

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
    @Autowired
    private ElasticComponent elasticComponent;
    @Autowired
    private JwtService jwtService;
    /**
     * 跟踪记录页面
     * @param jwt
     * @param trackingPageInputDTO
     * @return
     */
    public ServiceResult pageTrackingView(String jwt, TrackingPageInputDTO trackingPageInputDTO){
        PageViewDTOEntity pageViewDTOEntity = PageViewDTOEntity.builder().time( Calendar.getInstance(Locale.CHINA).getTime() )
                .jwt(jwt)
                .trackingPageInputDTO( trackingPageInputDTO ).build();
        String jsonObject = JSONObject.toJSONString( pageViewDTOEntity );
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
     * @param pageViewDTOEntity
     * @return
     */
    public Boolean receivePageView(PageViewDTOEntity pageViewDTOEntity){
        TrackingPageInputDTO trackingPageInputDTO = pageViewDTOEntity.getTrackingPageInputDTO();
        Integer customerId = jwtService.getCustomerId(pageViewDTOEntity.getJwt());

        TrackingPageViewEntity trackingPageViewEntity = TrackingPageViewEntity.builder()
                .action( trackingPageInputDTO.getTitle() )
                .uniqueId( trackingPageInputDTO.getUniqueId() )
                .customerId( customerId )
                .scene( trackingPageInputDTO.getScene() )
                .path(trackingPageInputDTO.getPath())
                .ip( trackingPageInputDTO.getIp() )
                .campaign( trackingPageInputDTO.getCampaign() )
                .merchantId( trackingPageInputDTO.getMerchantId() )
                .storeId( trackingPageInputDTO.getStoreId() )
                .model( trackingPageInputDTO.getModel() != null? trackingPageInputDTO.getModel():"***" )
                .size( trackingPageInputDTO.getSize() != null? trackingPageInputDTO.getSize():"***" )
                .data( DataConvertUtil.objectConvertJson(trackingPageInputDTO.getData()) )
                .createTime( pageViewDTOEntity.getTime() )
                .createDate( pageViewDTOEntity.getTime() )
                .build();

        TrackingPageViewEntity trackingPageViewEntityNew = trackingPageViewRepository.saveAndFlush( trackingPageViewEntity );
        if( trackingPageViewEntityNew.getTId() != null ){
            //推送ES
            String body = JSON.toJSONString(trackingPageViewEntityNew);
            elasticComponent.pushDocument(TRACKING_PAGE_INDEX,TRACKING_PAGE_INDEX_TYPE,trackingPageViewEntityNew.getTId().toString(),body);

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
            if( isCalculateCampaign && trackingPageInputDTO.getCampaign() != null && !"".equals(trackingPageInputDTO.getCampaign()) ){
                trackingSenderComponent.sendTracking( "sync.page.calculate.campaign_page", JSONObject.toJSONString( trackingPageViewEntityNew ) );
            }

            return true;
        }else{
            return false;
        }
    }

}
