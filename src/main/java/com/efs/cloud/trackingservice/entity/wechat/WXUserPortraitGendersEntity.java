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
@Table(name = "wx_user_portrait_genders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WXUserPortraitGendersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer portraitGenderId;
    private Integer merchantId;
    private Integer storeId;
    @Temporal(TemporalType.DATE)
    @JSONField(format = "yyyy-MM-dd")
    private Date refDate;
    private Integer genderId;
    private String genderName;
    private Integer genderValue;
    private Byte isNew;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
