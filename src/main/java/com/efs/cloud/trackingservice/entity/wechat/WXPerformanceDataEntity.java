package com.efs.cloud.trackingservice.entity.wechat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
/**
 * @author jabez.huang
 */
@Entity
@Table(name = "wx_performance_data")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXPerformanceDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer performanceId;
    private Integer merchantId;
    private Integer storeId;
    private Date refDate;
    private Integer moduleId;
    private String moduleName;
    private String searchField;
    private String searchValue;
    private Integer returnId;
    private String returnZh;
    private String returnValue;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
