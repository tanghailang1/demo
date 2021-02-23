package com.efs.cloud.trackingservice.repository.wechat;

import com.efs.cloud.trackingservice.entity.wechat.WXDailyRetainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface WXDailyRetainRepository extends JpaRepository<WXDailyRetainEntity,Integer> {

    /**
     * 根据商户日期查询
     * @param merchantId
     * @param storeId
     * @param refDate
     * @return
     */
    WXDailyRetainEntity findByMerchantIdAndStoreIdAndRefDate(Integer merchantId, Integer storeId, Date refDate);
}