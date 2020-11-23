package com.efs.cloud.trackingservice.repository.tracking;

import com.efs.cloud.trackingservice.entity.tracking.TrackingPageViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author jabez.huang
 */
public interface TrackingPageViewRepository extends JpaRepository<TrackingPageViewEntity,Integer> {


    /**
     * 查询单天商户店铺单个customer
     * @param createDate
     * @param merchantId
     * @param storeId
     * @param customerId
     * @return
     */
    List<TrackingPageViewEntity> findByCreateDateAndMerchantIdAndStoreIdAndCustomerId(Date createDate, Integer merchantId, Integer storeId, Integer customerId);

    /**
     * 单天商户店铺 List
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingPageViewEntity> findByMerchantIdAndStoreIdAndCreateDate(Integer merchantId, Integer storeId, Integer createDate);

    /**
     * 查找unionId是否存在
     * @param uniqueId
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingPageViewEntity> findByUniqueIdAndMerchantIdAndStoreIdAndCreateDate(String uniqueId, Integer merchantId, Integer storeId, Date createDate);

    /**
     * 查询渠道是否存在
     * @param scene
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingPageViewEntity> findBySceneAndMerchantIdAndStoreIdAndCreateDate(Integer scene, Integer merchantId, Integer storeId, Date createDate);

    /**
     * 查询Action页面标题是否存在
     * @param action
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingPageViewEntity> findByActionAndMerchantIdAndStoreIdAndCreateDate(String action, Integer merchantId, Integer storeId, Date createDate);

    /**
     * 查询Path是否存在
     * @param path
     * @param merchantId
     * @param storeId
     * @param createDate
     * @return
     */
    List<TrackingPageViewEntity> findByPathAndMerchantIdAndStoreIdAndCreateDate(String path, Integer merchantId, Integer storeId, Date createDate);
}
