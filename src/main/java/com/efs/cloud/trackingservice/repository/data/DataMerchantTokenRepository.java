package com.efs.cloud.trackingservice.repository.data;

import com.efs.cloud.trackingservice.entity.data.DataMerchantTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DataMerchantTokenRepository extends CrudRepository<DataMerchantTokenEntity, Integer> {

    DataMerchantTokenEntity findAllByMerchantId(Integer merchantId);

    List<DataMerchantTokenEntity> findAll();
}
