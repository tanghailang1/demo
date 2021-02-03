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
@Table(name = "wx_daily_retain")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXDailyRetainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dailyRetainId;
    private Integer merchantId;
    private Integer storeId;
    @Temporal(TemporalType.DATE)
    @JSONField(format = "yyyy-MM-dd")
    private Date refDate;
    private Integer visitUvNew;
    private Integer visitUv;
    private String contentNew;
    private String content;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
