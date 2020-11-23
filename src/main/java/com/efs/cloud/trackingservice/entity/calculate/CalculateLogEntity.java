package com.efs.cloud.trackingservice.entity.calculate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author jabez.huang
 */

@Entity
@Table(name = "calculate_log")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateLogEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer calculateLog;
    private String type;
    @Column(name = "content", columnDefinition = "json", nullable = true)
    private String content;
    private Date createTime;
}
