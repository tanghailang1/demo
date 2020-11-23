package com.efs.cloud.trackingservice.entity.tracking;

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
@Table(name = "tracking_event_cart")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrackingEventCartEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tcId;
    private String itemName;
    private Integer itemId;
    private Integer valueCode;
    private String valueName;
    private String skuCode;
    private Integer itemPrice;
    private Integer scene;
    private String campaign;
    private String uniqueId;
    private Integer customerId;
    private Integer merchantId;
    private Integer storeId;
    private String data;
    @Temporal(TemporalType.DATE)
    private Date createDate;
    private Date createTime;
}
