package com.efs.cloud.trackingservice.dto.wechat;

import lombok.Data;

/**
 * @author jabez.huang
 */
@Data
public class WXDateInputDTO {
    private String beginDate;
    private String endDate;
    private String appId;
}
