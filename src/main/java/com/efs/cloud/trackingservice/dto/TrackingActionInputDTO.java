package com.efs.cloud.trackingservice.dto;

import lombok.Data;

/**
 * @author jabez.huang
 */
@Data
public class TrackingActionInputDTO {
    private String value;
    private String path;
    private Integer scene;
    private String campaign;
    private String uniqueId;
    private Integer customerId;
    private Integer merchantId;
    private Integer storeId;
    private Object data;
}
