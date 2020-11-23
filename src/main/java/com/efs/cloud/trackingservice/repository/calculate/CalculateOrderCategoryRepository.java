package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderCategoryEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculateOrderCategoryRepository extends JpaRepository<CalculateOrderCategoryEntity, Integer> {

    /**
     * 查询单商户店铺渠道分类
     * @param categoryId
     * @param scene
     * @param merchantId
     * @param storeId
     * @param date
     * @param hour
     * @param status
     * @return
     */
    CalculateOrderCategoryEntity findByCategoryIdAndSceneAndMerchantIdAndStoreIdAndDateAndHourAndStatus(Integer categoryId, Integer scene, Integer merchantId, Integer storeId, Date date, Integer hour,String status);

}
