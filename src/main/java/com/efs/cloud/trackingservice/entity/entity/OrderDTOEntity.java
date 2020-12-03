package com.efs.cloud.trackingservice.entity.entity;

import com.efs.cloud.trackingservice.dto.TrackingOrderInputDTO;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
/**
 * @author jabez.huang
 */
@Builder
@Data
public class OrderDTOEntity {
    private String orderStatus;
    private String jwt;
    private Date time;
    private TrackingOrderInputDTO trackingOrderInputDTO;
}
