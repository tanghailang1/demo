package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateCartSkuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculateCartSkuRepository extends JpaRepository<CalculateCartSkuEntity, Integer> {

    /**
     * 查询商户店铺下商品Id
     * @param skuCode
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @return
     */
    CalculateCartSkuEntity findBySkuCodeAndDateAndHourAndMerchantIdAndStoreId(String skuCode, Date date, Integer hour, Integer merchantId, Integer storeId);

}
