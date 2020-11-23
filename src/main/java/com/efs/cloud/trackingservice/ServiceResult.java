package com.efs.cloud.trackingservice;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;


/**
 * @author jabez.huang
 */
@Builder
@Data
public class ServiceResult {

    /**
     * 状态码
     */
    @Builder.Default()
    private Integer code = 200;

    /**
     * 信息
     */
    @Builder.Default()
    private String msg = "成功";

    /**
     * 数据
     */
    @Builder.Default()
    private Object data = "";

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
