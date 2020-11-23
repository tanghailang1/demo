package com.efs.cloud.trackingservice.entity.calculate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import javax.persistence.*;
import java.io.Serializable;

/**
 * @author jabez.huang
 */

@Entity
@Table(name = "calculate_page_scene")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculatePageSceneEntity  implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer csId;
    @Temporal(TemporalType.DATE)
    private Date date;
    @Column(columnDefinition = "smallint")
    private Integer hour;
    private Integer merchantId;
    private Integer storeId;
    private Integer scene;
    private Integer pvCount;
    private Integer uvCount;
}
