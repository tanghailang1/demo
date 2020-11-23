package com.efs.cloud.trackingservice.dto;

import com.efs.cloud.trackingservice.entity.entity.OrderItemDTOEntity;
import lombok.Data;

import java.util.List;

/**
 * @author jabez.huang
 */

@Data
public class TrackingOrderInputDTO {
    private Integer orderAmount;
    private Integer orderShippingFee;
    private Integer orderDiscountAmount;
    private List<OrderItemDTOEntity> orderItems;
    private Integer orderSubtotal;
    private String ip;
    private Integer scene;
    private String campaign;
    private String uniqueId;
    private String orderId;
    private Integer customerId;
    private Integer merchantId;
    private Integer storeId;
    private Object data;
}
