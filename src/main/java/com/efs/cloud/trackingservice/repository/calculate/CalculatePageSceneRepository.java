package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculatePageSceneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculatePageSceneRepository extends JpaRepository<CalculatePageSceneEntity,Integer> {

    /**
     * 查询单个小时返回渠道实体
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @param scene
     * @return
     */
    CalculatePageSceneEntity findByDateAndHourAndMerchantIdAndStoreIdAndScene(Date date, Integer hour, Integer merchantId, Integer storeId, Integer scene);
}
