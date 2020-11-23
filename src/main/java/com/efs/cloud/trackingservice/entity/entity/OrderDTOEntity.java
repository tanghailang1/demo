package com.efs.cloud.trackingservice.entity.entity;

import com.efs.cloud.trackingservice.dto.TrackingOrderInputDTO;
import lombok.Builder;
import lombok.Data;

/**
 * @author jabez.huang
 */
@Builder
@Data
public class OrderDTOEntity {
    private String orderStatus;
    private TrackingOrderInputDTO trackingOrderInputDTO;
}
