package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateActionSearchEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateCartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculateCartItemRepository extends JpaRepository<CalculateCartItemEntity, Integer> {

    /**
     * 查询商户店铺下商品Id
     * @param itemId
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @return
     */
    CalculateCartItemEntity findByItemIdAndDateAndHourAndMerchantIdAndStoreId(Integer itemId, Date date, Integer hour, Integer merchantId, Integer storeId);

}
