package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderAmountEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculateOrderItemRepository extends JpaRepository<CalculateOrderItemEntity, Integer> {

    /**
     * 查询单商户店铺渠道商品
     * @param itemId
     * @param scene
     * @param merchantId
     * @param storeId
     * @param date
     * @param hour
     * @param status
     * @return
     */
    CalculateOrderItemEntity findByItemIdAndSceneAndMerchantIdAndStoreIdAndDateAndHourAndStatus(Integer itemId, Integer scene, Integer merchantId, Integer storeId, Date date, Integer hour, String status);

}
