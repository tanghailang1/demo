package com.efs.cloud.trackingservice.entity.calculate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author jabez.huang
 */

@Entity
@Table(name = "calculate_campaign")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateCampaignEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer campaignCalculateId;
    private String campaignName;
    private Integer pvCount;
    private Integer uvCount;
    private Integer customerCount;
    private Integer cartCount;
    private Integer createOrderCount;
    private Integer createOrderAmount;
    private Integer orderCount;
    private Integer orderAmount;
    @Temporal(TemporalType.DATE)
    private Date createDate;
    @Column(columnDefinition = "smallint")
    private Integer hour;
    private Integer merchantId;
    private Integer storeId;

}
