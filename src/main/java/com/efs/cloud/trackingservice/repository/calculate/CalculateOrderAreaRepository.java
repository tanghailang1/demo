package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author maxun
 */
public interface CalculateOrderAreaRepository extends JpaRepository<CalculateOrderAreaEntity, Integer> {

    /**
     * 查询单商户店铺渠道地区
     * @param date
     * @param hour
     * @param scene
     * @param merchantId
     * @param storeId
     * @return
     */
    CalculateOrderAreaEntity findByCreateDateAndCityAndHourAndSceneAndMerchantIdAndStoreId(Date date,String city, Integer hour, Integer scene, Integer merchantId, Integer storeId);

}
