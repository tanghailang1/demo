package com.efs.cloud.trackingservice.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @author maxun
 */

@Data
public class TrackingLogInputDTO {

    private Integer merchantId;
    private Integer storeId;
    private String type;
    private String content;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private Object data;
}
