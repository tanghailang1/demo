package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculatePageActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculatePageActionRepository extends JpaRepository<CalculatePageActionEntity,Integer> {

    /**
     * 查询单个小时返回Action实体
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @param action
     * @return
     */
    CalculatePageActionEntity findByDateAndHourAndMerchantIdAndStoreIdAndAction(Date date, Integer hour, Integer merchantId, Integer storeId, String action);
}
