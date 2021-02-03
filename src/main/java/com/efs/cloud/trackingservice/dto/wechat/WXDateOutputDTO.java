package com.efs.cloud.trackingservice.dto.wechat;

import lombok.Builder;
import lombok.Data;

/**
 * @author jabez.huang
 */
@Builder
@Data
public class WXDateOutputDTO {
    private String begin_date;
    private String end_date;
}
