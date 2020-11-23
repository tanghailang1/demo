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
@Table(name = "calculate_order_item")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateOrderItemEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;
    @Temporal(TemporalType.DATE)
    private Date date;
    @Column(columnDefinition = "smallint")
    private Integer hour;
    private Integer scene;
    private Integer itemId;
    private String itemName;
    private Integer itemOrderCount;
    private Integer itemAmount;
    private Integer customerCount;
    private Integer merchantId;
    private Integer storeId;
    private String status;
}
