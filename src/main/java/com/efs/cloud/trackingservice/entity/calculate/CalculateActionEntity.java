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
@Table(name = "calculate_action")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateActionEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer actionId;
    private String type;
    private Integer pvCount;
    private Integer uvCount;
    private Integer merchantId;
    private Integer storeId;
    @Column(columnDefinition = "smallint")
    private Integer hour;
    @Temporal(TemporalType.DATE)
    private Date date;
}
