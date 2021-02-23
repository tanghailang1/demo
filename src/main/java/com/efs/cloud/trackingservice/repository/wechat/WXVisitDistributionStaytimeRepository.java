package com.efs.cloud.trackingservice.repository.wechat;

import com.efs.cloud.trackingservice.entity.wechat.WXVisitDistributionStaytimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author jabez.huang
 */
public interface WXVisitDistributionStaytimeRepository extends JpaRepository<WXVisitDistributionStaytimeEntity,Integer> {

    /**
     * 根据商户查询数据
     * @param merchantId
     * @param storeId
     * @param refDate
     * @return
     */
    List<WXVisitDistributionStaytimeEntity> findByMerchantIdAndStoreIdAndRefDate(Integer merchantId, Integer storeId, Date refDate);
}