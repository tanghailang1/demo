package com.efs.cloud.trackingservice.entity.wechat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "wx_user_portrait_city")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXUserPortraitCityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer portraitCityId;
    private Integer merchantId;
    private Integer storeId;
    @Temporal(TemporalType.DATE)
    @JSONField(format = "yyyy-MM-dd")
    private Date refDate;
    private Integer cityId;
    private String cityName;
    private Integer cityValue;
    private Byte isNew;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
