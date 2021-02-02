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
@Table(name = "calculate_action_cms")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateActionCmsEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cmsId;
    @Temporal(TemporalType.DATE)
    private Date date;
    @Column(columnDefinition = "smallint")
    private Integer hour;
    private String handleName;
    private String cmsIndex;
    private Integer pvCount;
    private Integer uvCount;
    private Integer merchantId;
    private Integer storeId;
}
