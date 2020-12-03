package com.efs.cloud.trackingservice.entity.entity;

import com.efs.cloud.trackingservice.dto.TrackingPageInputDTO;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
/**
 * @author jabez.huang
 */
@Data
@Builder
public class PageViewDTOEntity {
    private String jwt;
    private Date time;
    private TrackingPageInputDTO trackingPageInputDTO;
}
