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
@Table(name = "tracking_event_action")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrackingEventActionEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taId;
    private String eventType;
    private String eventMessage;
    private String eventValue;
    private String path;
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
