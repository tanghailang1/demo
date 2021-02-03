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
@Table(name = "wx_user_portrait_ages")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXUserPortraitAgesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer portraitAgeId;
    private Integer merchantId;
    private Integer storeId;
    @Temporal(TemporalType.DATE)
    @JSONField(format = "yyyy-MM-dd")
    private Date refDate;
    private Integer ageId;
    private String ageName;
    private Integer ageValue;
    private Byte isNew;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
