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
@Table(name = "wx_visit_page")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXVisitPageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer visitPageId;
    private Integer merchantId;
    private Integer storeId;
    @Temporal(TemporalType.DATE)
    @JSONField(format = "yyyy-MM-dd")
    private Date refDate;
    private String pagePath;
    private Integer pageVisitPv;
    private Integer pageVisitUv;
    @Column(columnDefinition = "decimal(10,4)")
    private Double pageStaytimePv;
    private Integer entrypagePv;
    private Integer exitpagePv;
    private Integer pageSharePv;
    private Integer pageShareUv;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
