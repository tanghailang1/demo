package com.efs.cloud.trackingservice.repository.wechat;

import com.efs.cloud.trackingservice.entity.wechat.WXVisitDistributionInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WXVisitDistributionInfoRepository extends JpaRepository<WXVisitDistributionInfoEntity,Integer> {

    /**
     * 根据类型查询结果
     * @param distributionType
     * @return
     */
    List<WXVisitDistributionInfoEntity> findByDistributionType(String distributionType);
}
