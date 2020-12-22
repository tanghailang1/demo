package com.efs.cloud.trackingservice.entity.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 商户tokenEntity
 * @author maxun
 */
@Entity
@Table(name = "data_merchant_token")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataMerchantTokenEntity {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dataMerchantId;
    private Integer merchantId;
    private String merchantName;
    @Column(nullable = false, columnDefinition = "longtext")
    private String token;
    private Integer tmStoreId;
    private Timestamp expiredTime;
    private Timestamp updateTime;

}
