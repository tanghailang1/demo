package com.efs.cloud.trackingservice.entity.wechat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author jabez.huang
 */
@Entity
@Table(name = "wx_monthly_retain")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXMonthlyRetainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer monthlyId;
    private Integer merchantId;
    private Integer storeId;
    private String refDate;
    private Integer visitUvNew;
    private Integer visitUv;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
