package com.efs.cloud.trackingservice.service.calculate;

import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.IPUtils;
import com.efs.cloud.trackingservice.component.ElasticComponent;
import com.efs.cloud.trackingservice.entity.calculate.*;
import com.efs.cloud.trackingservice.entity.entity.OrderItemDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventOrderEntity;
import com.efs.cloud.trackingservice.enums.OrderStatusEnum;
import com.efs.cloud.trackingservice.repository.calculate.*;
import com.efs.cloud.trackingservice.repository.tracking.TrackingEventOrderRepository;
import com.efs.cloud.trackingservice.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.efs.cloud.trackingservice.Global.SALES_ORDER_INDEX;
import static com.efs.cloud.trackingservice.Global.TRACKING_ORDER_INDEX;

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
    private CalculateOrderAreaRepository calculateOrderAreaRepository;
    @Autowired
    private CalculateLogRepository calculateLogRepository;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private CalculateOrderCustomerRepository calculateOrderCustomerRepository;
    /**
     * 统计渠道订单金额
     * @param trackingEventOrderEntity
     * @return
     */
    public Boolean receiveCalculateOrderAmount(TrackingEventOrderEntity trackingEventOrderEntity){
        Date currentTime = trackingEventOrderEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findCustomerId( trackingEventOrderEntity, currentTime );

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
        Date currentTime = trackingEventOrderEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findCustomerId( trackingEventOrderEntity, currentTime );

        List<OrderItemDTOEntity> orderDTOEntityList = trackingEventOrderEntity.getOrderItems();

        for( OrderItemDTOEntity orderItemDTOEntity : orderDTOEntityList ){
            ElasticComponent.SearchDocumentResponse trackingEventOrderEntitySdr = elasticsearchService.findByItemIdAndMerchantIdAndStoreIdAndCreateDate(
                    TRACKING_ORDER_INDEX,
                    orderItemDTOEntity.getItemId(),
                    trackingEventOrderEntity.getMerchantId(),
                    trackingEventOrderEntity.getStoreId(),
                    currentTime
            );
            if( trackingEventOrderEntitySdr.getHits().getTotal() > 0 ){
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
        Date currentTime = trackingEventOrderEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findCustomerId( trackingEventOrderEntity, currentTime );
        List<OrderItemDTOEntity> orderDTOEntityList = trackingEventOrderEntity.getOrderItems();

        for( OrderItemDTOEntity orderItemDTOEntity : orderDTOEntityList ){
            ElasticComponent.SearchDocumentResponse trackingEventOrderEntitySdr = elasticsearchService.findByCategoryIdAndMerchantIdAndStoreIdAndCreateDate(
                    TRACKING_ORDER_INDEX,
                    orderItemDTOEntity.getCategoryId(),
                    trackingEventOrderEntity.getMerchantId(),
                    trackingEventOrderEntity.getStoreId(),
                    currentTime
            );
            if( trackingEventOrderEntitySdr.getHits().getTotal() > 0 ){
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

    /**
     * 统计Order所属城市
     * @param trackingEventOrderEntity
     * @return
     */
    public boolean receiveCalculateOrderArea(TrackingEventOrderEntity trackingEventOrderEntity){
        Date currentTime = trackingEventOrderEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        String ip = trackingEventOrderEntity.getIp();
        String city = IPUtils.getAddressCity(ip);
        CalculateOrderAreaEntity calculateOrderAreaEntity = calculateOrderAreaRepository.findByCreateDateAndCityAndHourAndSceneAndMerchantIdAndStoreId(
                currentTime,
                city,
                hour,
                trackingEventOrderEntity.getScene(),
                trackingEventOrderEntity.getMerchantId(),
                trackingEventOrderEntity.getStoreId()
        );
        Integer createOrderCount = 0;
        Integer createOrderAmount = 0;
        Integer orderCount = 0;
        Integer orderAmount = 0;
        if( OrderStatusEnum.ORDER_PAY.getValue().equals(trackingEventOrderEntity.getStatus())){
            createOrderCount = 1;
            createOrderAmount = trackingEventOrderEntity.getOrderAmount();
        }else{
            orderCount = 1;
            orderAmount = trackingEventOrderEntity.getOrderAmount();
        }

        if( calculateOrderAreaEntity != null ){
            CalculateOrderAreaEntity calculateOrderAreaEntityExists = CalculateOrderAreaEntity.builder()
                    .orderAreaId( calculateOrderAreaEntity.getOrderAreaId() )
                    .city( calculateOrderAreaEntity.getCity() )
                    .scene(calculateOrderAreaEntity.getScene())
                    .createOrderCount( calculateOrderAreaEntity.getCreateOrderCount() + createOrderCount )
                    .createOrderAmount( calculateOrderAreaEntity.getCreateOrderAmount() + createOrderAmount )
                    .orderCount( calculateOrderAreaEntity.getOrderCount() + orderCount )
                    .orderAmount( calculateOrderAreaEntity.getOrderAmount() + orderAmount )
                    .createDate( calculateOrderAreaEntity.getCreateDate() )
                    .hour( calculateOrderAreaEntity.getHour() )
                    .merchantId( calculateOrderAreaEntity.getMerchantId() )
                    .storeId( calculateOrderAreaEntity.getStoreId() )
                    .build();
            CalculateOrderAreaEntity isSave = calculateOrderAreaRepository.saveAndFlush( calculateOrderAreaEntityExists );
            if( isSave == null  ){
                calculateLogRepository.saveAndFlush(
                        CalculateLogEntity.builder().type("calculate_order_area").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculateOrderAreaEntity calculateOrderAreaEntityNew = CalculateOrderAreaEntity.builder()
                    .city( city )
                    .scene(trackingEventOrderEntity.getScene())
                    .createOrderCount( createOrderCount )
                    .createOrderAmount( createOrderAmount )
                    .orderCount( orderCount )
                    .orderAmount( orderAmount )
                    .createDate( currentTime )
                    .hour( hour )
                    .merchantId( trackingEventOrderEntity.getMerchantId() )
                    .storeId( trackingEventOrderEntity.getStoreId() )
                    .build();
            CalculateOrderAreaEntity isSave = calculateOrderAreaRepository.saveAndFlush( calculateOrderAreaEntityNew );
            if( isSave == null  ) {
                calculateLogRepository.saveAndFlush(
                        CalculateLogEntity.builder().type("calculate_order_area").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime(currentTime).build()
                );
            }
        }

        return true;
    }

    /**
     * 统计Order新老客购买情况
     * @param trackingEventOrderEntity
     * @return
     */
    public boolean receiveCalculateOrderCustomer(TrackingEventOrderEntity trackingEventOrderEntity){
        Date currentTime = trackingEventOrderEntity.getCreateTime();

        CalculateOrderCustomerEntity calculateOrderCustomerEntity = calculateOrderCustomerRepository.findByDateAndMerchantIdAndStoreId(
                currentTime,
                trackingEventOrderEntity.getMerchantId(),
                trackingEventOrderEntity.getStoreId()
        );
        Integer oldCustomerOrderCount = 0;
        Integer oldCustomerOrderAmount = 0;
        Integer newCustomerOrderCount = 0;
        Integer newCustomerOrderAmount = 0;
        ElasticComponent.SearchDocumentResponse cloudOrderSdr = elasticsearchService.findByIndexAndCustomerIdAndStatus(SALES_ORDER_INDEX,trackingEventOrderEntity.getCustomerId());
        if (cloudOrderSdr.getHits().getTotal() > 0) {
            oldCustomerOrderCount = 1;
            oldCustomerOrderAmount = trackingEventOrderEntity.getOrderAmount();
        }else {
            newCustomerOrderCount = 1;
            newCustomerOrderAmount = trackingEventOrderEntity.getOrderAmount();
        }
        if (calculateOrderCustomerEntity != null) {
            CalculateOrderCustomerEntity calculateOrderCustomerEntityExists = CalculateOrderCustomerEntity.builder()
                    .orderCustomerId( calculateOrderCustomerEntity.getOrderCustomerId() )
                    .date( calculateOrderCustomerEntity.getDate() )
                    .merchantId( calculateOrderCustomerEntity.getMerchantId() )
                    .storeId( calculateOrderCustomerEntity.getStoreId() )
                    .orderCount( calculateOrderCustomerEntity.getOrderCount() + 1 )
                    .orderAmount( calculateOrderCustomerEntity.getOrderAmount() + trackingEventOrderEntity.getOrderAmount() )
                    .oldCustomerOrderCount( calculateOrderCustomerEntity.getOldCustomerOrderCount() + oldCustomerOrderCount )
                    .oldCustomerOrderAmount( calculateOrderCustomerEntity.getOldCustomerOrderAmount() + oldCustomerOrderAmount )
                    .newCustomerOrderCount( calculateOrderCustomerEntity.getNewCustomerOrderCount() + newCustomerOrderCount )
                    .newCustomerOrderAmount( calculateOrderCustomerEntity.getNewCustomerOrderAmount() + newCustomerOrderAmount)
                    .build();
            CalculateOrderCustomerEntity isSave = calculateOrderCustomerRepository.saveAndFlush( calculateOrderCustomerEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                        CalculateLogEntity.builder().type("calculate_order_customer").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculateOrderCustomerEntity calculateOrderCustomerEntityNew = CalculateOrderCustomerEntity.builder()
                    .date( currentTime )
                    .merchantId( trackingEventOrderEntity.getMerchantId() )
                    .storeId( trackingEventOrderEntity.getStoreId() )
                    .orderCount( 1 )
                    .orderAmount( trackingEventOrderEntity.getOrderAmount() )
                    .oldCustomerOrderCount(oldCustomerOrderCount )
                    .oldCustomerOrderAmount(oldCustomerOrderAmount )
                    .newCustomerOrderCount( newCustomerOrderCount )
                    .newCustomerOrderAmount(newCustomerOrderAmount)
                    .build();
            CalculateOrderCustomerEntity isSave = calculateOrderCustomerRepository.saveAndFlush( calculateOrderCustomerEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                        CalculateLogEntity.builder().type("calculate_order_customer").content(JSONObject.toJSONString(trackingEventOrderEntity)).createTime( currentTime ).build()
                );
            }
        }

        return true;
    }

    private Integer findCustomerId(TrackingEventOrderEntity trackingEventOrderEntity, Date date){
        ElasticComponent.SearchDocumentResponse trackingEventOrderEntitySdr = elasticsearchService.findByIndexByCreateDateAndMerchantIdAndStoreIdAndCustomerId( TRACKING_ORDER_INDEX,date,
                trackingEventOrderEntity.getMerchantId(), trackingEventOrderEntity.getStoreId(),trackingEventOrderEntity.getCustomerId()  );
        Integer union = 1;
        if( trackingEventOrderEntitySdr.getHits().getTotal() > 1 ){
            union = 0;
        }
        return union;
    }

}
