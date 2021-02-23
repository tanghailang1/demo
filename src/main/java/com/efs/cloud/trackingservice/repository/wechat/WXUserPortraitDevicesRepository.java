package com.efs.cloud.trackingservice.repository.wechat;

import com.efs.cloud.trackingservice.entity.wechat.WXUserPortraitDevicesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jabez.huang
 */
public interface WXUserPortraitDevicesRepository extends JpaRepository<WXUserPortraitDevicesEntity,Integer> {
}
