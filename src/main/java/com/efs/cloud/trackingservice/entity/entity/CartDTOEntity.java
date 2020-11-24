package com.efs.cloud.trackingservice.entity.entity;

import com.efs.cloud.trackingservice.dto.TrackingCartInputDTO;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
/**
 * @author jabez.huang
 */
@Builder
@Data
public class CartDTOEntity {
    private Date time;
    private TrackingCartInputDTO trackingCartInputDTO;
}
