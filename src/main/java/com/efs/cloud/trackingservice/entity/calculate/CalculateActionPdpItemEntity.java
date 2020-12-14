package com.efs.cloud.trackingservice.entity.calculate;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maxun
 */
@Entity
@Table(name = "calculate_action_pdp_item")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateActionPdpItemEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pdpItemId;
    @Temporal(TemporalType.DATE)
    private Date date;
    @Column(columnDefinition = "smallint")
    private Integer hour;
    private Integer itemId;
    private String itemName;
    private String itemCode;
    private String imageSrc;
    private String campaignName;
    private Integer pvCount;
    private Integer uvCount;
    private Integer merchantId;
    private Integer storeId;
    private Integer itemOrderCount;
    private Integer itemAmount;
}
