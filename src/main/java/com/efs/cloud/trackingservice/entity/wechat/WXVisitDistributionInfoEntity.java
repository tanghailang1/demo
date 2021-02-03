package com.efs.cloud.trackingservice.entity.wechat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author jabez.huang
 */
@Entity
@Table(name = "wx_visit_distribution_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXVisitDistributionInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer distributionId;
    private String distributionType;
    private Integer itemKey;
    private String itemName;
    private String sceneList;
}
