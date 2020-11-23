package com.efs.cloud.trackingservice.repository.tracking;

import com.efs.cloud.trackingservice.entity.tracking.TrackingEventCartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author jabez.huang
 */
public interface TrackingEventCartRepository extends JpaRepository<TrackingEventCartEntity,Integer> {

    /**
     * 查询单商户单个Item加购
     * @param itemId
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventCartEntity> findByItemIdAndMerchantIdAndStoreIdAndCreateDate(Integer itemId, Integer merchantId, Integer storeId, Date createDate);


    /**
     * 查询单商户SkuCode加购
     * @param skuCode
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventCartEntity> findBySkuCodeAndMerchantIdAndStoreIdAndCreateDate(String skuCode, Integer merchantId, Integer storeId, Date createDate);

    /**
     * 查询单商户单个CC加购
     * @param valueCode
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventCartEntity> findByValueCodeAndMerchantIdAndStoreIdAndCreateDate(Integer valueCode, Integer merchantId, Integer storeId, Date createDate);

    /**
     * 查询单商户UniqueId加购
     * @param uniqueId
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventCartEntity> findByUniqueIdAndMerchantIdAndStoreIdAndCreateDate(String uniqueId, Integer merchantId, Integer storeId, Date createDate);

    /**
     * 查询单商户CustomerId加购
     * @param customerId
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventCartEntity> findByCustomerIdAndMerchantIdAndStoreIdAndCreateDate(Integer customerId, Integer merchantId, Integer storeId, Date createDate);
}
