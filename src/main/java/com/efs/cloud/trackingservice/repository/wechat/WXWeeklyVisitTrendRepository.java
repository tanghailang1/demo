package com.efs.cloud.trackingservice.repository.wechat;

import com.efs.cloud.trackingservice.entity.wechat.WXWeeklyVisitTrendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface WXWeeklyVisitTrendRepository extends JpaRepository<WXWeeklyVisitTrendEntity,Integer> {

    /**
     * 根据商户查询数据
     * @param merchantId
     * @param storeId
     * @param refDate
     * @return
     */
    WXWeeklyVisitTrendEntity findByMerchantIdAndStoreIdAndRefDate(Integer merchantId, Integer storeId, String refDate);
}
