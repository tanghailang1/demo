package com.efs.cloud.trackingservice.repository.wechat;

import com.efs.cloud.trackingservice.entity.wechat.WXPerformanceDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jabez.huang
 */
public interface WXPerformanceDataRepository extends JpaRepository<WXPerformanceDataEntity,Integer> {
}
