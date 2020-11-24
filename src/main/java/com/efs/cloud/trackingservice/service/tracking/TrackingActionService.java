package com.efs.cloud.trackingservice.service.tracking;

import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.component.TrackingSenderComponent;
import com.efs.cloud.trackingservice.dto.TrackingActionInputDTO;
import com.efs.cloud.trackingservice.entity.entity.ActionDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventActionEntity;
import com.efs.cloud.trackingservice.enums.EventTypeEnum;
import com.efs.cloud.trackingservice.repository.tracking.TrackingEventActionRepository;
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
public class TrackingActionService {

    @Value("${sync.calculate.action}")
    private Boolean isCalculateAction;
    @Value("${sync.calculate.action_search}")
    private Boolean isCalculateActionSearch;
    @Value("${sync.calculate.action_share}")
    private Boolean isCalculateActionShare;
    @Autowired
    private TrackingSenderComponent trackingSenderComponent;
    @Autowired
    private TrackingEventActionRepository trackingEventActionRepository;

    /**
     * 设置页面行为事件
     * @param trackingActionInputDTO
     * @return
     */
    public ServiceResult eventTrackingAction(TrackingActionInputDTO trackingActionInputDTO, EventTypeEnum eventTypeEnum){
        String jsonObject = JSONObject.toJSONString( ActionDTOEntity.builder().time(
                Calendar.getInstance(Locale.CHINA).getTime()).type( eventTypeEnum.getValue() ).value( eventTypeEnum.getMessage() )
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

        TrackingEventActionEntity trackingEventActionEntity = TrackingEventActionEntity.builder().eventType( actionDTOEntity.getType() )
                .eventValue( trackingActionInputDTO.getValue() )
                .eventMessage( actionDTOEntity.getValue() )
                .path( trackingActionInputDTO.getPath() )
                .scene( trackingActionInputDTO.getScene() )
                .campaign( trackingActionInputDTO.getCampaign() )
                .uniqueId( trackingActionInputDTO.getUniqueId() )
                .customerId( trackingActionInputDTO.getCustomerId() )
                .merchantId( trackingActionInputDTO.getMerchantId() )
                .storeId( trackingActionInputDTO.getStoreId() )
                .data( DataConvertUtil.objectConvertJson(trackingActionInputDTO.getData()) )
                .createDate( actionDTOEntity.getTime() )
                .createTime( actionDTOEntity.getTime() ).build();
        TrackingEventActionEntity trackingActionEntityNew = trackingEventActionRepository.saveAndFlush( trackingEventActionEntity );

        if( trackingActionEntityNew != null ){
            //action
            if( isCalculateAction ){
                trackingSenderComponent.sendTracking("sync.action.calculate.action", JSONObject.toJSONString( trackingActionEntityNew ));
            }

            //action search
            if( isCalculateActionSearch ){
                if( trackingActionEntityNew.getEventType().equals(EventTypeEnum.SEARCH.getValue()) ){
                    trackingSenderComponent.sendTracking("sync.action.calculate.action_search", JSONObject.toJSONString( trackingActionEntityNew ));
                }
            }

            //action share
            if( isCalculateActionShare ){
                if( trackingActionEntityNew.getEventType().equals(EventTypeEnum.SHARE_CARD.getValue()) ||
                    trackingActionEntityNew.getEventType().equals(EventTypeEnum.SHARE_PLAYBILL.getValue())) {
                    trackingSenderComponent.sendTracking("sync.action.calculate.action_share", JSONObject.toJSONString(trackingActionEntityNew));
                }
            }
            return true;
        }else{
            return false;
        }
    }

}
