package com.efs.cloud.trackingservice.repository.wechat;

import com.efs.cloud.trackingservice.entity.wechat.WXMonthlyRetainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jabez.huang
 */
public interface WXMonthlyRetainRepository extends JpaRepository<WXMonthlyRetainEntity,Integer> {

    /**
     * 根据商户日期查询
     * @param merchantId
     * @param storeId
     * @param refDate
     * @return
     */
    WXMonthlyRetainEntity findByMerchantIdAndStoreIdAndRefDate(Integer merchantId, Integer storeId,String refDate);
}
