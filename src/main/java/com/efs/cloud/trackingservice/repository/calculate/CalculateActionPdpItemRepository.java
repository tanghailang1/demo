package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculateActionPdpItemEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateActionSearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author maxun
 */
public interface CalculateActionPdpItemRepository extends JpaRepository<CalculateActionPdpItemEntity, Integer> {

    /**
     *
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @param itemId
     * @param CampaignName
     * @return
     */
    CalculateActionPdpItemEntity findByDateAndHourAndMerchantIdAndStoreIdAndItemIdAndCampaignName(Date date, Integer hour, Integer merchantId, Integer storeId, Integer itemId,String CampaignName);

}
