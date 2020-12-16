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
    public ServiceResult eventTrackingAction(String jwt, TrackingActionInputDTO trackingActionInputDTO, EventTypeEnum eventTypeEnum) throws ParseException {
        String jsonObject = JSONObject.toJSONString( ActionDTOEntity.builder().time(
                DateUtil.getStringGMT8Time(Calendar.getInstance(Locale.CHINA).getTime())).type( eventTypeEnum.getValue() ).value( eventTypeEnum.getMessage() )
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
        TrackingEventActionEntity trackingActionEntityNew = trackingEventActionRepository.saveAndFlush( trackingEventActionEntity );

        if( trackingActionEntityNew != null ){
            //推送ES
            String body = JSON.toJSONString(trackingActionEntityNew);
            elasticComponent.pushDocument(TRACKING_ACTION_INDEX,TRACKING_ACTION_INDEX_TYPE,trackingActionEntityNew.getTaId().toString(),body);

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

            //action pdp_item
            if( isCalculateActionPdpItem ){
                if( trackingActionEntityNew.getEventType().equals(EventTypeEnum.PDP_ITEM.getValue()) ){
                    trackingSenderComponent.sendTracking("sync.action.calculate.action_pdp_item", JSONObject.toJSONString( trackingActionEntityNew ));
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
