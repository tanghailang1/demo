package com.efs.cloud.trackingservice.service.tracking;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.component.ElasticComponent;
import com.efs.cloud.trackingservice.component.TrackingSenderComponent;
import com.efs.cloud.trackingservice.dto.TrackingLogInputDTO;
import com.efs.cloud.trackingservice.entity.entity.LogDTOEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import static com.efs.cloud.trackingservice.Global.TRACKING_LOG_INDEX;
import static com.efs.cloud.trackingservice.Global.TRACKING_LOG_INDEX_TYPE;

/**
 * @author maxun
 */
@Slf4j
@Service
public class TrackingLogService {

    @Autowired
    private TrackingSenderComponent trackingSenderComponent;
    @Autowired
    private ElasticComponent elasticComponent;

    /**
     * 记录加购事件
     * @param trackingLogInputDTO
     * @return
     */
    public ServiceResult eventTrackingLog(TrackingLogInputDTO trackingLogInputDTO){
        String jsonObject = JSONObject.toJSONString( LogDTOEntity.builder().time(Calendar.getInstance(Locale.CHINA).getTime())
                .trackingLogInputDTO(trackingLogInputDTO).build() );
        trackingSenderComponent.sendTracking( "sync.log.tracking", jsonObject );
        return ServiceResult.builder().code(200).data(null).msg("Success").build();
    }

    /**
     * 存储基础事件
     * @param logDTOEntity
     * @return
     */
    public Boolean receiveEventOrder(LogDTOEntity logDTOEntity) {
        TrackingLogInputDTO trackingLogInputDTO = logDTOEntity.getTrackingLogInputDTO();
        trackingLogInputDTO.setCreateTime(logDTOEntity.getTime());
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        //推送ES
        String body = JSON.toJSONString(trackingLogInputDTO);
        elasticComponent.pushDocument(TRACKING_LOG_INDEX,TRACKING_LOG_INDEX_TYPE,uuid,body);
        return true;
    }
}
