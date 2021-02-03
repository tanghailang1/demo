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
@Table(name = "wx_weekly_retain")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXWeeklyRetainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer weeklyId;
    private Integer merchantId;
    private Integer storeId;
    private String refDate;
    private Integer visitUvNew;
    private Integer visitUv;
    private String content;
    private String contentNew;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
