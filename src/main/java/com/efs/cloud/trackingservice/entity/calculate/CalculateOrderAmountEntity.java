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
@Table(name = "calculate_order_amount")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateOrderAmountEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderSceneId;
    @Temporal(TemporalType.DATE)
    private Date date;
    @Column(columnDefinition = "smallint")
    private Integer hour;
    private Integer scene;
    private Integer orderCount;
    private Integer orderAmount;
    private Integer customerCount;
    private Integer merchantId;
    private Integer storeId;
    private String status;
}
