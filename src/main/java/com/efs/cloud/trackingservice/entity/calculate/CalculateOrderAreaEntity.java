package com.efs.cloud.trackingservice.entity.calculate;

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
@Table(name = "calculate_order_area")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateOrderAreaEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderAreaId;
    private String city;
    private Integer scene;
    private Integer createOrderCount;
    private Integer createOrderAmount;
    private Integer orderCount;
    private Integer orderAmount;
    private Integer merchantId;
    private Integer storeId;
    @Temporal(TemporalType.DATE)
    private Date createDate;
    @Column(columnDefinition = "smallint")
    private Integer hour;

}
