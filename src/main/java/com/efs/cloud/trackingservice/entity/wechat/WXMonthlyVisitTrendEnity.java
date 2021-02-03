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
@Table(name = "wx_monthly_visit_trend")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXMonthlyVisitTrendEnity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer monthlyTrendId;
    private Integer merchantId;
    private Integer storeId;
    private String refDate;
    private Integer sessionCnt;
    private Integer visitPv;
    private Integer visitUv;
    private Integer visitUvNew;
    @Column(columnDefinition = "decimal(10,4)")
    private Double stayTimeUv;
    @Column(columnDefinition = "decimal(10,4)")
    private Double stayTimeSession;
    @Column(columnDefinition = "decimal(10,4)")
    private Double visitDepth;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
