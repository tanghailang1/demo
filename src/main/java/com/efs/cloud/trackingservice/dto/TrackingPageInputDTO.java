package com.efs.cloud.trackingservice.dto;

import lombok.Data;

/**
 * @author jabez.huang
 */

@Data
public class TrackingPageInputDTO {
    private String title;
    private String path;
    private String uniqueId;
    private Integer customerId;
    private Integer scene;
    private String ip;
    private String campaign;
    private Integer merchantId;
    private Integer storeId;
    private String model;
    private String size;
    private Object data;
}
