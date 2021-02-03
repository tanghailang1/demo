package com.efs.cloud.trackingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jabez.huang
 */
@Builder
@Data
public class CloudStoreConfigOutputDTO {
    private Integer cloudMerchantId;
    private Integer cloudMerchantStoreId;

}
