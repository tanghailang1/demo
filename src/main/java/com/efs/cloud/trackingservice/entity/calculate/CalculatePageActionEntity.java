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
@Table(name = "calculate_page_action")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculatePageActionEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer caId;
    @Temporal(TemporalType.DATE)
    private Date date;
    @Column(columnDefinition = "smallint")
    private Integer hour;
    private Integer merchantId;
    private Integer storeId;
    private String action;
    private Integer pvCount;
    private Integer uvCount;
}
