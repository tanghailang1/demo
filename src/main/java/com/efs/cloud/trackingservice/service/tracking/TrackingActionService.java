package com.efs.cloud.trackingservice.service.tracking;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.component.ElasticComponent;
import com.efs.cloud.trackingservice.component.TrackingSenderComponent;
import com.efs.cloud.trackingservice.dto.TrackingActionInputDTO;
import com.efs.cloud.trackingservice.entity.entity.ActionDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventActionEntity;
import com.efs.cloud.trackingservice.enums.EventTypeEnum;
import com.efs.cloud.trackingservice.repository.tracking.TrackingEventActionRepository;
import com.efs.cloud.trackingservice.service.JwtService;
import com.efs.cloud.trackingservice.util.DataConvertUtil;
import com.efs.cloud.trackingservice.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import static com.efs.cloud.trackingservice.Global.*;

/**
 * @author jabez.huang
 */

@Slf4j
@Service
public class TrackingActionService {

    @Value("${sync.calculate.action}")
    private Boolean isCalculateAction;
    @Value("${sync.calculate.action_search}")
    private Boolean isCalculateActionSearch;
    @Value("${sync.calculate.action_share}")
    private Boolean isCalculateActionShare;
    @Value("${sync.calculate.action_cms}")
    private Boolean isCalculateActionCms;
    @Value("${sync.calculate.action_pdp_item}")
    private Boolean isCalculateActionPdpItem;
    @Autowired
    private TrackingSenderComponent trackingSenderComponent;
    @Autowired
    private TrackingEventActionRepository trackingEventActionRepository;
    @Autowired
    private ElasticComponent elasticComponent;
    @Autowired
    private JwtService jwtService;

    /**
     * 设置页面行为事件
     * @param jwt
     * @param trackingActionInputDTO
     * @return
     */
    public ServiceResult eventTrackingAction(String jwt, TrackingActionInputDTO trackingActionInputDTO, EventTypeEnum eventTypeEnum) {
        String jsonObject = JSONObject.toJSONString( ActionDTOEntity.builder().time(
                Calendar.getInstance(Locale.CHINA).getTime()).type( eventTypeEnum.getValue() ).value( eventTypeEnum.getMessage() )
                .jwt(jwt)
                .trackingActionInputDTO( trackingActionInputDTO ).build() );
        trackingSenderComponent.sendTracking( "sync.action.tracking.action", jsonObject );
        return ServiceResult.builder().code(200).data(null).msg("Success").build();
    }

    /**
     * 存储基础事件
     * @param actionDTOEntity
     * @return
     */
    public Boolean receiveEventAction(ActionDTOEntity actionDTOEntity){
        TrackingActionInputDTO trackingActionInputDTO = actionDTOEntity.getTrackingActionInputDTO();
        Integer customerId = jwtService.getCustomerId(actionDTOEntity.getJwt());

        TrackingEventActionEntity trackingEventActionEntity = TrackingEventActionEntity.builder().eventType( actionDTOEntity.getType() )
                .eventValue( trackingActionInputDTO.getValue() )
                .eventMessage( actionDTOEntity.getValue() )
                .path( trackingActionInputDTO.getPath() )
                .scene( trackingActionInputDTO.getScene() )
                .campaign( trackingActionInputDTO.getCampaign() )
                .uniqueId( trackingActionInputDTO.getUniqueId() )
                .customerId( customerId )
                .merchantId( trackingActionInputDTO.getMerchantId() )
                .storeId( trackingActionInputDTO.getStoreId() )
                .data( DataConvertUtil.objectConvertJson(trackingActionInputDTO.getData()) )
                .createDate( actionDTOEntity.getTime() )
                .createTime( actionDTOEntity.getTime() ).build();

        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        if( trackingEventActionEntity != null ){
            //推送ES
            String body = JSON.toJSONString(trackingEventActionEntity);
            elasticComponent.pushDocument(TRACKING_ACTION_INDEX,TRACKING_ACTION_INDEX_TYPE,uuid,body);

            //action
            if( isCalculateAction ){
                trackingSenderComponent.sendTracking("sync.action.calculate.action", JSONObject.toJSONString( trackingEventActionEntity ));
            }

            //action search
            if( isCalculateActionSearch ){
                if( trackingEventActionEntity.getEventType().equals(EventTypeEnum.SEARCH.getValue()) ){
                    trackingSenderComponent.sendTracking("sync.action.calculate.action_search", JSONObject.toJSONString( trackingEventActionEntity ));
                }
            }

            //action pdp_item
            if( isCalculateActionPdpItem ){
                if( trackingEventActionEntity.getEventType().equals(EventTypeEnum.PDP_ITEM.getValue()) ){
                    trackingSenderComponent.sendTracking("sync.action.calculate.action_pdp_item", JSONObject.toJSONString( trackingEventActionEntity ));
                }
            }

            //action share
            if( isCalculateActionShare ){
                if( trackingEventActionEntity.getEventType().equals(EventTypeEnum.SHARE_CARD.getValue()) ||
                        trackingEventActionEntity.getEventType().equals(EventTypeEnum.SHARE_PLAYBILL.getValue())) {
                    trackingSenderComponent.sendTracking("sync.action.calculate.action_share", JSONObject.toJSONString(trackingEventActionEntity));
                }
            }

            //action cms
            if( isCalculateActionCms ){
                if( trackingEventActionEntity.getEventType().equals(EventTypeEnum.CLICK_CMS.getValue())) {
                    trackingSenderComponent.sendTracking("sync.action.calculate.action_cms", JSONObject.toJSONString(trackingEventActionEntity));
                }
            }
            return true;
        }else{
            return false;
        }
    }

}
