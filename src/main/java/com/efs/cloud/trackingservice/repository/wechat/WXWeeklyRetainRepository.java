package com.efs.cloud.trackingservice.repository.wechat;

import com.efs.cloud.trackingservice.entity.wechat.WXWeeklyRetainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jabez.huang
 */
public interface WXWeeklyRetainRepository extends JpaRepository<WXWeeklyRetainEntity,Integer> {

    /**
     * 根据商户日期查询
     * @param merchantId
     * @param storeId
     * @param refDate
     * @return
     */
    WXWeeklyRetainEntity findByMerchantIdAndStoreIdAndRefDate(Integer merchantId, Integer storeId,String refDate);
}
