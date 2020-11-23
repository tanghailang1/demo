package com.efs.cloud.trackingservice.enums;

import lombok.Getter;

/**
 * @author jabez.huang
 */

@Getter
public enum OrderStatusEnum {
    ORDER_PAY("ORDER_PAY","下单"),
    PAY_SUCCESS("PAY_SUCCESS","支付成功");

    private String value;
    private String message;
    OrderStatusEnum(String value, String message){
        this.value = value;
        this.message = message;
    }
}
