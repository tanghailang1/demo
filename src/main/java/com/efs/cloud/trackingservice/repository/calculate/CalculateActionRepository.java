package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculateActionRepository extends JpaRepository<CalculateActionEntity,Integer> {

    /**
     * 查询是否存在Action
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @param type
     * @return
     */
    CalculateActionEntity findByDateAndHourAndMerchantIdAndStoreIdAndType(Date date, Integer hour, Integer merchantId, Integer storeId, String type);

}
