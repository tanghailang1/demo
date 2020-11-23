package com.efs.cloud.trackingservice.repository.calculate;

import com.efs.cloud.trackingservice.entity.calculate.CalculatePageViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author jabez.huang
 */
public interface CalculatePageViewRepository extends JpaRepository<CalculatePageViewEntity,Integer> {
    /**
     * 查询单小时内的商户店铺的实体
     * @param date
     * @param hour
     * @param merchantId
     * @param storeId
     * @return
     */
    CalculatePageViewEntity findByDateAndHourAndMerchantIdAndStoreId(Date date, Integer hour, Integer merchantId, Integer storeId);

}
