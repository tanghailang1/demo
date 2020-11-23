package com.efs.cloud.trackingservice.repository.tracking;

import com.efs.cloud.trackingservice.entity.tracking.TrackingEventOrderEntity;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @author jabez.huang
 */
public interface TrackingEventOrderRepository extends JpaRepository<TrackingEventOrderEntity,Integer> {

    /**
     * 查询单商户某渠道支付笔数与金额
     * @param scene
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventOrderEntity> findBySceneAndMerchantIdAndStoreIdAndCreateDate(Integer scene, Integer merchantId, Integer storeId, Date createDate);


    /**
     * 查询单商户统计UniqueId
     * @param uniqueId
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventOrderEntity> findByUniqueIdAndMerchantIdAndStoreIdAndCreateDate(String uniqueId, Integer merchantId, Integer storeId, Date createDate);

    /**
     * 查询单商户统计支付人数
     * @param customerId
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingEventOrderEntity> findByCustomerIdAndMerchantIdAndStoreIdAndCreateDate(Integer customerId, Integer merchantId, Integer storeId, Date createDate);

    /**
     * 查询单商户单品销售
     * @param itemId
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    @Query(value="select * from tracking_event_order where JSON_CONTAINS(order_items,JSON_OBJECT('itemId', ?1)) and merchant_id = ?2 and store_id = ?3 and create_date = ?4", nativeQuery = true)
    List<TrackingEventOrderEntity> findByItemIdAndMerchantIdAndStoreId(Integer itemId, Integer merchantId, Integer storeId, String createDate);

    /**
     * 查询OrderItems集合里的CategoryId
     * @param id
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    @Query(value="select * from tracking_event_order where JSON_CONTAINS(order_items,JSON_OBJECT('categoryId', ?1)) and merchant_id = ?2 and store_id = ?3 and create_date = ?4", nativeQuery = true)
    List<TrackingEventOrderEntity> findByCategoryIdAndMerchantIdAndStoreIdAndCreateDate(Integer id, Integer merchantId, Integer storeId, String createDate);
}
