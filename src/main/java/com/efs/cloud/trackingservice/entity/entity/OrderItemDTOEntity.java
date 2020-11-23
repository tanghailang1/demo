package com.efs.cloud.trackingservice.entity.entity;

import lombok.Data;

/**
 * @author jabez.huang
 */

@Data
public class OrderItemDTOEntity {
    private Integer itemId;
    private String itemSku;
    private String itemName;
    private Integer categoryId;
    private String categoryName;
    private Integer orderQty;
    private Integer rowTotal;
    private Integer unitPrice;
    private Integer discountAmount;
}
