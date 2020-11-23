package com.efs.cloud.trackingservice.service.calculate;

import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.entity.calculate.CalculateLogEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderAmountEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderCategoryEntity;
import com.efs.cloud.trackingservice.entity.calculate.CalculateOrderItemEntity;
import com.efs.cloud.trackingservice.entity.entity.OrderItemDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventOrderEntity;
import com.efs.cloud.trackingservice.repository.calculate.CalculateLogRepository;
import com.efs.cloud.trackingservice.repository.calculate.CalculateOrderAmountRepository;
import com.efs.cloud.trackingservice.repository.calculate.CalculateOrderCategoryRepository;
import com.efs.cloud.trackingservice.repository.calculate.CalculateOrderItemRepository;
import com.efs.cloud.trackingservice.repository.tracking.TrackingEventOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jabez.huang
 */

@Slf4j
@Service
public class CalculateOrderService {

    @Autowired
    private TrackingEventOrderRepository trackingEventOrderRepository;
    @Autowired
    private CalculateOrderAmountRepository calculateOrderAmountRepository;
    @Autowired
    private CalculateOrderItemRepository calculateOrderItemRepository;
    @Autowired
    private CalculateOrderCategoryRepository calculateOrderCategoryRepository;
    @Autowired
    private CalculateLogRepository calculateLogRepository;

    /**
     * 统计渠道订单金额
     * @param trackingEventOrderEntity
     * @return
     */
    public Boolean receiveCalculateOrderAmount(TrackingEventOrderEntity trackingEventOrderEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Integer union = findCustomerId( trackingEventOrderEntity );

        Date currentTime = calendar.getTime();
        Integer hour =  calendar.get(Calendar.HOUR_OF_DAY);

        CalculateOrderAmountEntity calculateOrderAmountEntity = calculateOrderAmountRepository.findByDateAndHourAndSceneAndMerchantIdAndStoreIdAndStatus(
                currentTime, hour, trackingEventOrderEntity.getScene(), trackingEventOrderEntity.getMerchantId(), trackingEventOrderEntity.getStoreId(), trackingEventOrderEntity.getStatus()  );
        if (calculateOrderAmountEntity != null) {
            CalculateOrderAmountEntity calculateOrderAmountEntityExists = CalculateOrderAmountEntity.builder()
                    .orderSceneId( calculateOrderAmountEntity.getOrderSceneId() )
                    .date( calculateOrderAmountEntity.getDate() )
                    .hour( calculateOrderAmountEntity.getHour() )
                    .scene( calculateOrderAmountEntity.getScene() )
                    .merchantId( calculateOrderAmountEntity.getMerchantId() )
                    .storeId( calculateOrderAmountEntity.getStoreId() )
                    .orderCount( calculateOrderAmountEntity.getOrderCount() + 1 )
                    .orderAmount( calculateOrderAmountEntity.getOrderAmount() + trackingEventOrderEntity.getOrderAmount() )
                    .customerCount( calculateOrderAmountEntity.getCustomerCount() + union )
                    .status( calculateOrderAmountEntity.getStatus() )
                    .build();
            CalculateOrderAmountEntity isSave = calculateOrderAmountRepository.saveAndFlush( calculateOrderAmountEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_order_amount").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculateOrderAmountEntity calculateOrderAmountEntityNew = CalculateOrderAmountEntity.builder()
                    .date( currentTime )
                    .hour( hour )
                    .scene( trackingEventOrderEntity.getScene() )
                    .merchantId( trackingEventOrderEntity.getMerchantId() )
                    .storeId( trackingEventOrderEntity.getStoreId() )
                    .orderCount( 1 )
                    .orderAmount( trackingEventOrderEntity.getOrderAmount() )
                    .customerCount( 1 )
                    .status( trackingEventOrderEntity.getStatus() )
                    .build();
            CalculateOrderAmountEntity isSave = calculateOrderAmountRepository.saveAndFlush( calculateOrderAmountEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_order_amount").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                );
            }
        }

        return true;
    }

    /**
     * 统计订单商品销售
     * @param trackingEventOrderEntity
     * @return
     */
    public Boolean receiveCalculateOrderItem(TrackingEventOrderEntity trackingEventOrderEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date currentTime = calendar.getTime();
        Integer hour =  calendar.get(Calendar.HOUR_OF_DAY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Integer union = findCustomerId( trackingEventOrderEntity );
        List<OrderItemDTOEntity> orderDTOEntityList = trackingEventOrderEntity.getOrderItems();

        for( OrderItemDTOEntity orderItemDTOEntity : orderDTOEntityList ){
            List<TrackingEventOrderEntity> trackingEventOrderEntityList = trackingEventOrderRepository.findByItemIdAndMerchantIdAndStoreId(
                    orderItemDTOEntity.getItemId(),
                    trackingEventOrderEntity.getMerchantId(),
                    trackingEventOrderEntity.getStoreId(),
                    sdf.format( currentTime )
            );
            if( trackingEventOrderEntityList.size() > 0 ){
                CalculateOrderItemEntity calculateOrderItemEntity = calculateOrderItemRepository.findByItemIdAndSceneAndMerchantIdAndStoreIdAndDateAndHourAndStatus(
                        orderItemDTOEntity.getItemId(),
                        trackingEventOrderEntity.getScene(),
                        trackingEventOrderEntity.getMerchantId(),
                        trackingEventOrderEntity.getStoreId(),
                        currentTime,
                        hour,
                        trackingEventOrderEntity.getStatus()
                );
                if( calculateOrderItemEntity != null ){
                    CalculateOrderItemEntity calculateOrderItemEntityExists = CalculateOrderItemEntity.builder()
                            .orderItemId( calculateOrderItemEntity.getOrderItemId() )
                            .itemId( calculateOrderItemEntity.getItemId() )
                            .itemName( calculateOrderItemEntity.getItemName() )
                            .scene( calculateOrderItemEntity.getScene() )
                            .itemOrderCount( calculateOrderItemEntity.getItemOrderCount() + orderItemDTOEntity.getOrderQty() )
                            .itemAmount( calculateOrderItemEntity.getItemAmount() + orderItemDTOEntity.getRowTotal() )
                            .customerCount( calculateOrderItemEntity.getCustomerCount() + union )
                            .merchantId( calculateOrderItemEntity.getMerchantId() )
                            .storeId( calculateOrderItemEntity.getStoreId() )
                            .date( currentTime )
                            .hour( hour )
                            .status( calculateOrderItemEntity.getStatus() )
                            .build();
                    CalculateOrderItemEntity isSave = calculateOrderItemRepository.saveAndFlush( calculateOrderItemEntityExists );
                    if( isSave == null ){
                        calculateLogRepository.saveAndFlush(
                            CalculateLogEntity.builder().type("calculate_order_item").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                        );
                    }
                }else{
                    CalculateOrderItemEntity calculateOrderItemEntityNew = CalculateOrderItemEntity.builder()
                            .itemId( orderItemDTOEntity.getItemId() )
                            .itemName( orderItemDTOEntity.getItemName() )
                            .scene( trackingEventOrderEntity.getScene() )
                            .itemOrderCount( orderItemDTOEntity.getOrderQty() )
                            .itemAmount( orderItemDTOEntity.getRowTotal() )
                            .customerCount( 1 )
                            .merchantId( trackingEventOrderEntity.getMerchantId() )
                            .storeId( trackingEventOrderEntity.getStoreId() )
                            .date( currentTime )
                            .hour( hour )
                            .status( trackingEventOrderEntity.getStatus() )
                            .build();
                    CalculateOrderItemEntity isSave = calculateOrderItemRepository.saveAndFlush( calculateOrderItemEntityNew );
                    if( isSave == null ){
                        calculateLogRepository.saveAndFlush(
                            CalculateLogEntity.builder().type("calculate_order_item").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                        );
                    }
                }
            }else{
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_order_item").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                );
            }
        }
        return true;
    }

    /**
     * 统计订单商品所属分类
     * @param trackingEventOrderEntity
     * @return
     */
    public Boolean receiveCalculateOrderCategory(TrackingEventOrderEntity trackingEventOrderEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Integer union = findCustomerId( trackingEventOrderEntity );
        List<OrderItemDTOEntity> orderDTOEntityList = trackingEventOrderEntity.getOrderItems();

        Date currentTime = calendar.getTime();
        Integer hour =  calendar.get(Calendar.HOUR_OF_DAY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for( OrderItemDTOEntity orderItemDTOEntity : orderDTOEntityList ){
            List<TrackingEventOrderEntity> trackingEventOrderEntityList = trackingEventOrderRepository.findByCategoryIdAndMerchantIdAndStoreIdAndCreateDate(
                    orderItemDTOEntity.getCategoryId(),
                    trackingEventOrderEntity.getMerchantId(),
                    trackingEventOrderEntity.getStoreId(),
                    sdf.format( currentTime )
            );
            if( trackingEventOrderEntityList.size() > 0 ){
                CalculateOrderCategoryEntity calculateOrderCategoryEntity = calculateOrderCategoryRepository.findByCategoryIdAndSceneAndMerchantIdAndStoreIdAndDateAndHourAndStatus(
                        orderItemDTOEntity.getCategoryId(),
                        trackingEventOrderEntity.getScene(),
                        trackingEventOrderEntity.getMerchantId(),
                        trackingEventOrderEntity.getStoreId(),
                        currentTime,
                        hour,
                        trackingEventOrderEntity.getStatus()
                );
                if( calculateOrderCategoryEntity != null ){
                    CalculateOrderCategoryEntity calculateOrderCategoryEntityExists = CalculateOrderCategoryEntity.builder()
                            .orderCategoryId( calculateOrderCategoryEntity.getOrderCategoryId() )
                            .categoryId( calculateOrderCategoryEntity.getCategoryId() )
                            .categoryName( calculateOrderCategoryEntity.getCategoryName() )
                            .scene( calculateOrderCategoryEntity.getScene() )
                            .categoryCount( calculateOrderCategoryEntity.getCategoryCount() + orderItemDTOEntity.getOrderQty() )
                            .categoryAmount( calculateOrderCategoryEntity.getCategoryAmount() + orderItemDTOEntity.getRowTotal() )
                            .customerCount( calculateOrderCategoryEntity.getCustomerCount() + union )
                            .merchantId( calculateOrderCategoryEntity.getMerchantId() )
                            .storeId( calculateOrderCategoryEntity.getStoreId() )
                            .date( currentTime )
                            .hour( hour )
                            .status( calculateOrderCategoryEntity.getStatus() )
                            .build();
                    CalculateOrderCategoryEntity isSave = calculateOrderCategoryRepository.saveAndFlush( calculateOrderCategoryEntityExists );
                    if( isSave == null ){
                        calculateLogRepository.saveAndFlush(
                            CalculateLogEntity.builder().type("calculate_order_category").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                        );
                    }
                }else{
                    CalculateOrderCategoryEntity calculateOrderCategoryEntityNew = CalculateOrderCategoryEntity.builder()
                            .categoryId( orderItemDTOEntity.getCategoryId() )
                            .categoryName( orderItemDTOEntity.getCategoryName() )
                            .scene( trackingEventOrderEntity.getScene() )
                            .categoryCount( orderItemDTOEntity.getOrderQty() )
                            .categoryAmount( orderItemDTOEntity.getRowTotal() )
                            .customerCount( 1 )
                            .merchantId( trackingEventOrderEntity.getMerchantId() )
                            .storeId( trackingEventOrderEntity.getStoreId() )
                            .date( currentTime )
                            .hour( hour )
                            .status( trackingEventOrderEntity.getStatus() )
                            .build();
                    CalculateOrderCategoryEntity isSave = calculateOrderCategoryRepository.saveAndFlush( calculateOrderCategoryEntityNew );
                    if( isSave == null ){
                        calculateLogRepository.saveAndFlush(
                            CalculateLogEntity.builder().type("calculate_order_category").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                        );
                    }
                }
            }else{
                CalculateLogEntity.builder().type("calculate_order_category").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build();
            }
        }
        return true;
    }

    private Integer findCustomerId(TrackingEventOrderEntity trackingEventOrderEntity){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        List<TrackingEventOrderEntity> trackingEventOrderEntityList = trackingEventOrderRepository.findByCustomerIdAndMerchantIdAndStoreIdAndCreateDate( trackingEventOrderEntity.getCustomerId(),
                trackingEventOrderEntity.getMerchantId(), trackingEventOrderEntity.getStoreId(), calendar.getTime() );
        Integer union = 1;
        if( trackingEventOrderEntityList.size() > 1 ){
            union = 0;
        }
        return union;
    }

}
