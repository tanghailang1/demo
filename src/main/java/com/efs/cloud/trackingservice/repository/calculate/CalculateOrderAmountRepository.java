package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderAmountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculateOrderAmountRepository extends JpaRepository<CalculateOrderAmountEntity, Integer> {

    /**
     * 查询单商户店铺渠道金额
     * @param date
     * @param hour
     * @param scene
     * @param merchantId
     * @param storeId
     * @param status
     * @return
     */
    CalculateOrderAmountEntity findByDateAndHourAndSceneAndMerchantIdAndStoreIdAndStatus(Date date, Integer hour, Integer scene, Integer merchantId, Integer storeId, String status);

}
