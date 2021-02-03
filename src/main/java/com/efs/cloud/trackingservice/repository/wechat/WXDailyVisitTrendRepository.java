package com.efs.cloud.trackingservice.repository.wechat;

import com.efs.cloud.trackingservice.entity.wechat.WXDailyVisitTrendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface WXDailyVisitTrendRepository extends JpaRepository<WXDailyVisitTrendEntity,Integer> {

    /**
     * 根据商户查询数据
     * @param merchantId
     * @param storeId
     * @param refDate
     * @return
     */
    WXDailyVisitTrendEntity findByMerchantIdAndStoreIdAndRefDate(Integer merchantId, Integer storeId, Date refDate);
}
