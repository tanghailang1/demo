package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculatePagePathEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculatePagePathRepository extends JpaRepository<CalculatePagePathEntity,Integer> {

    /**
     * 查询单个小时返回Path实体
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @param path
     * @return
     */
    CalculatePagePathEntity findByDateAndHourAndMerchantIdAndStoreIdAndPath(Date date, Integer hour, Integer merchantId, Integer storeId, String path);
}
