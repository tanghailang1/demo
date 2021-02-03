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
@Table(name = "wx_visit_distribution_depth")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXVisitDistributionDepthEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accessDepthId;
    private Integer merchantId;
    private Integer storeId;
    @Temporal(TemporalType.DATE)
    @JSONField(format = "yyyy-MM-dd")
    private Date refDate;
    private Integer accessKey;
    private String accessName;
    private Integer accessValue;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
