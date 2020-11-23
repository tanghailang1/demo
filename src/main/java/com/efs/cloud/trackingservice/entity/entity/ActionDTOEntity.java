package com.efs.cloud.trackingservice.entity.entity;

import com.efs.cloud.trackingservice.dto.TrackingActionInputDTO;
import lombok.Builder;
import lombok.Data;

/**
 * @author jabez.huang
 */
@Builder
@Data
public class ActionDTOEntity {
    private String type;
    private String value;
    private TrackingActionInputDTO trackingActionInputDTO;
}
