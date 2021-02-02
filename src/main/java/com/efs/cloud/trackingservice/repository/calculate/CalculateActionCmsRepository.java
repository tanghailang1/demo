package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateActionCmsEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateActionSearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculateActionCmsRepository extends JpaRepository<CalculateActionCmsEntity, Integer> {

    /**
     * 查询cms名称是否存在
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @param handleName
     * @param index
     * @return
     */
    CalculateActionCmsEntity findByDateAndHourAndMerchantIdAndStoreIdAndHandleNameAndCmsIndex(Date date, Integer hour, Integer merchantId, Integer storeId, String handleName, String index);

}
