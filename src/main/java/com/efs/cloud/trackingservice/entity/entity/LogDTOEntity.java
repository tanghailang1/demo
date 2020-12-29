package com.efs.cloud.trackingservice.entity.entity;

import com.efs.cloud.trackingservice.dto.TrackingLogInputDTO;
import com.efs.cloud.trackingservice.dto.TrackingOrderInputDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author maxun
 */
@Builder
@Data
public class LogDTOEntity {
    private Date time;
    private TrackingLogInputDTO trackingLogInputDTO;
}
