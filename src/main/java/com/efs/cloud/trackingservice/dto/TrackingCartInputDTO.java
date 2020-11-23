package com.efs.cloud.trackingservice.dto;

import lombok.Data;

/**
 * @author jabez.huang
 */
@Data
public class TrackingCartInputDTO {
    private String itemName;
    private Integer itemId;
    private Integer valueCode;
    private String skuCode;
    private String valueName;
    private Integer itemPrice;
    private Integer scene;
    private String campaign;
    private String uniqueId;
    private Integer customerId;
    private Integer merchantId;
    private Integer storeId;
    private Object data;
}
