package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderAreaEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderCustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author maxun
 */
public interface CalculateOrderCustomerRepository extends JpaRepository<CalculateOrderCustomerEntity, Integer> {

    /**
     * 查询单商户店铺新老客购买情况
     * @param date
     * @param merchantId
     * @param storeId
     * @return
     */
    CalculateOrderCustomerEntity findByDateAndMerchantIdAndStoreId(Date date,Integer merchantId, Integer storeId);

}
