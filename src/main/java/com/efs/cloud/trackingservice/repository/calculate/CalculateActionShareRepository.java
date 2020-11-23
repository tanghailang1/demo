package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateActionSearchEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateActionShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculateActionShareRepository extends JpaRepository<CalculateActionShareEntity,Integer> {


    /**
     * 查询单商户分享情况
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @param share
     * @return
     */
    CalculateActionShareEntity findByDateAndHourAndMerchantIdAndStoreIdAndShare(Date date, Integer hour, Integer merchantId, Integer storeId, String share);
}
