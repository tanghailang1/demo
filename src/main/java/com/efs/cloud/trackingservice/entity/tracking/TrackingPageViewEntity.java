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
@Table(name = "tracking_page_view")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrackingPageViewEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "t_id", columnDefinition = "bigint", nullable = true)
    private Integer tId;
    private String action;
    private String path;
    private String uniqueId;
    private Integer customerId;
    private String ip;
    private Integer scene;
    private String campaign;
    private Integer merchantId;
    private Integer storeId;
    private String model;
    private String size;
    private String data;
    private Date createTime;
    @Temporal(TemporalType.DATE)
    private Date createDate;
}
