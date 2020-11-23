package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateActionSearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculateActionSearchRepository extends JpaRepository<CalculateActionSearchEntity, Integer> {

    /**
     * 查询某个关键词是否存在
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @param keyword
     * @return
     */
    CalculateActionSearchEntity findByDateAndHourAndMerchantIdAndStoreIdAndKeyword(Date date, Integer hour, Integer merchantId, Integer storeId, String keyword);

}
