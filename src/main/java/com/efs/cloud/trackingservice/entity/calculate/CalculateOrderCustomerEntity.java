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
@Table(name = "calculate_order_customer")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateOrderCustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderCustomerId;
    @Temporal(TemporalType.DATE)
    private Date date;
    private Integer orderCount;
    private Integer orderAmount;
    private Integer oldCustomerOrderCount;
    private Integer oldCustomerOrderAmount;
    private Integer newCustomerOrderCount;
    private Integer newCustomerOrderAmount;
    private Integer merchantId;
    private Integer storeId;



}
