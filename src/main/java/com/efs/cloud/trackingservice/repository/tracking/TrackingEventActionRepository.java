package com.efs.cloud.trackingservice.repository.tracking;

import com.efs.cloud.trackingservice.entity.tracking.TrackingEventActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author jabez.huang
 */
public interface TrackingEventActionRepository extends JpaRepository<TrackingEventActionEntity,Integer> {

    /**
     * 查询单商户事件类型
     * @param eventType
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventActionEntity> findByEventTypeAndMerchantIdAndStoreIdAndCreateDate(String eventType, Integer merchantId, Integer storeId, Date createDate);

    /**
     * 查询单商户事件某个value类型
     * @param eventType
     * @param eventValue
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventActionEntity> findByEventTypeAndEventValueAndMerchantIdAndStoreIdAndCreateDate(String eventType, String eventValue, Integer merchantId, Integer storeId, Date createDate);


    /**
     * 查询UV计数
     * @param uniqueId
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventActionEntity> findByUniqueIdAndMerchantIdAndStoreIdAndCreateDate(String uniqueId, Integer merchantId, Integer storeId, Date createDate );

}
