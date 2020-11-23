package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateCampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculateCampaignRepository extends JpaRepository<CalculateCampaignEntity, Integer> {

    /**
     * 获取单个商户Campaign渠道
     * @param campaignName
     * @param createDate
     * @param hour
     * @param merchantId
     * @param storeId
     * @return
     */
    CalculateCampaignEntity findByCampaignNameAndCreateDateAndHourAndMerchantIdAndStoreId(String campaignName, Date createDate, Integer hour, Integer merchantId, Integer storeId);


}
