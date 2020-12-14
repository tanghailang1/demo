package com.efs.cloud.trackingservice.service.calculate;

import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.component.ElasticComponent;
import com.efs.cloud.trackingservice.entity.calculate.*;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventActionEntity;
import com.efs.cloud.trackingservice.enums.EventTypeEnum;
import com.efs.cloud.trackingservice.repository.calculate.*;
import com.efs.cloud.trackingservice.repository.tracking.TrackingEventActionRepository;
import com.efs.cloud.trackingservice.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.efs.cloud.trackingservice.Global.TRACKING_ACTION_INDEX;

/**
 * @author jabez.huang
 */

@Slf4j
@Service
public class CalculateActionService {

    @Autowired
    private TrackingEventActionRepository trackingEventActionRepository;
    @Autowired
    private CalculateActionRepository calculateActionRepository;
    @Autowired
    private CalculateActionSearchRepository calculateActionSearchRepository;
    @Autowired
    private CalculateActionShareRepository calculateActionShareRepository;
    @Autowired
    private CalculateLogRepository calculateLogRepository;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private CalculateActionPdpItemRepository calculateActionPdpItemRepository;
    @Autowired
    private CalculateOrderItemRepository calculateOrderItemRepository;

    /**
     * Action基础计算
     * @param trackingEventActionEntity
     * @return
     */
    public Boolean receiveCalculateAction(TrackingEventActionEntity trackingEventActionEntity){
        Date currentTime = trackingEventActionEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findUniqueId( trackingEventActionEntity, currentTime );

        CalculateActionEntity calculateActionEntity = calculateActionRepository.findByDateAndHourAndMerchantIdAndStoreIdAndType( currentTime,
                hour, trackingEventActionEntity.getMerchantId(), trackingEventActionEntity.getStoreId(), trackingEventActionEntity.getEventType() );

        if (calculateActionEntity != null) {
            CalculateActionEntity calculatePageViewEntityExists = CalculateActionEntity.builder()
                    .actionId( calculateActionEntity.getActionId() )
                    .type( calculateActionEntity.getType() )
                    .date( calculateActionEntity.getDate() )
                    .hour( calculateActionEntity.getHour() )
                    .merchantId( calculateActionEntity.getMerchantId() )
                    .storeId( calculateActionEntity.getStoreId() )
                    .pvCount( calculateActionEntity.getPvCount() + 1)
                    .uvCount( calculateActionEntity.getUvCount() + union )
                    .build();
            CalculateActionEntity isSave = calculateActionRepository.saveAndFlush( calculatePageViewEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_action").content(JSONObject.toJSONString(trackingEventActionEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculateActionEntity calculateActionEntityNew = CalculateActionEntity.builder()
                    .date( currentTime )
                    .hour( hour )
                    .type( trackingEventActionEntity.getEventType() )
                    .merchantId( trackingEventActionEntity.getMerchantId() )
                    .storeId( trackingEventActionEntity.getStoreId() )
                    .pvCount(1)
                    .uvCount(1)
                    .build();
            CalculateActionEntity isSave = calculateActionRepository.saveAndFlush( calculateActionEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_action").content(JSONObject.toJSONString(trackingEventActionEntity)).createTime( currentTime ).build()
                );
            }
        }
        return true;
    }

    /**
     * Action搜索统计
     * @param trackingEventActionEntity
     * @return
     */
    public Boolean receiveCalculateActionSearch(TrackingEventActionEntity trackingEventActionEntity){
        Date currentTime = trackingEventActionEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findUniqueId( trackingEventActionEntity, currentTime );

        CalculateActionSearchEntity calculateActionSearchEntity = calculateActionSearchRepository.findByDateAndHourAndMerchantIdAndStoreIdAndKeyword(
                currentTime,hour, trackingEventActionEntity.getMerchantId(),
                trackingEventActionEntity.getStoreId(),trackingEventActionEntity.getEventValue()
        );
        if (calculateActionSearchEntity != null) {
            CalculateActionSearchEntity calculateActionSearchEntityExists = CalculateActionSearchEntity.builder()
                    .searchId( calculateActionSearchEntity.getSearchId() )
                    .date( currentTime )
                    .hour( hour )
                    .merchantId( calculateActionSearchEntity.getMerchantId() )
                    .storeId( calculateActionSearchEntity.getStoreId() )
                    .pvCount( calculateActionSearchEntity.getPvCount() + 1)
                    .uvCount( calculateActionSearchEntity.getUvCount() + union )
                    .keyword( calculateActionSearchEntity.getKeyword() ).build();
            CalculateActionSearchEntity isSave = calculateActionSearchRepository.saveAndFlush( calculateActionSearchEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_action_search").content(JSONObject.toJSONString(trackingEventActionEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculateActionSearchEntity calculateActionSearchEntityNew = CalculateActionSearchEntity.builder()
                    .date( currentTime )
                    .hour( hour )
                    .merchantId( trackingEventActionEntity.getMerchantId() )
                    .storeId( trackingEventActionEntity.getStoreId() )
                    .keyword( trackingEventActionEntity.getEventValue() )
                    .pvCount( 1 )
                    .uvCount( 1 )
                    .build();
            CalculateActionSearchEntity isSave = calculateActionSearchRepository.saveAndFlush( calculateActionSearchEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_action_search").content(JSONObject.toJSONString(trackingEventActionEntity)).createTime( currentTime ).build()
                );
            }
        }
        return true;
    }

    /**
     * ActionPDP商品统计
     * @param trackingEventActionEntity
     * @return
     */
    public Boolean receiveCalculateActionPdpItem(TrackingEventActionEntity trackingEventActionEntity){
        Date currentTime = trackingEventActionEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findUniqueId( trackingEventActionEntity, currentTime );
        Integer itemAmount = 0;
        Integer itemOrderCount = 0;
        JSONObject valueJson = JSONObject.parseObject(trackingEventActionEntity.getEventValue());
        String itemId = valueJson.getString("itemId");
        CalculateOrderItemEntity calculateOrderItemEntity = calculateOrderItemRepository.findByItemIdAndSceneAndMerchantIdAndStoreIdAndDateAndHourAndStatus(
                Integer.parseInt(itemId),
                trackingEventActionEntity.getScene(),
                trackingEventActionEntity.getMerchantId(),
                trackingEventActionEntity.getStoreId(),
                currentTime,
                hour,
                "PAY_SUCCESS"
        );
        if (calculateOrderItemEntity != null){
            itemAmount = calculateOrderItemEntity.getItemAmount();
            itemOrderCount = calculateOrderItemEntity.getItemOrderCount();
        }

        CalculateActionPdpItemEntity calculateActionPdpItemEntity = calculateActionPdpItemRepository.findByDateAndHourAndMerchantIdAndStoreIdAndItemIdAndCampaignName(
                currentTime,
                hour,
                trackingEventActionEntity.getMerchantId(),
                trackingEventActionEntity.getStoreId(),
                Integer.parseInt(itemId),
                trackingEventActionEntity.getCampaign()
        );
        if (calculateActionPdpItemEntity != null) {
            CalculateActionPdpItemEntity calculateActionPdpItemEntityExists = CalculateActionPdpItemEntity.builder()
                    .pdpItemId( calculateActionPdpItemEntity.getPdpItemId() )
                    .date( currentTime )
                    .hour( hour )
                    .merchantId( calculateActionPdpItemEntity.getMerchantId() )
                    .campaignName(calculateActionPdpItemEntity.getCampaignName())
                    .itemAmount(itemAmount)
                    .itemOrderCount(itemOrderCount)
                    .storeId( calculateActionPdpItemEntity.getStoreId() )
                    .pvCount( calculateActionPdpItemEntity.getPvCount() + 1)
                    .uvCount( calculateActionPdpItemEntity.getUvCount() + union )
                    .itemCode(calculateActionPdpItemEntity.getItemCode())
                    .imageSrc(calculateActionPdpItemEntity.getImageSrc())
                    .itemName(calculateActionPdpItemEntity.getItemName())
                    .itemId( calculateActionPdpItemEntity.getItemId() ).build();
            CalculateActionPdpItemEntity isSave = calculateActionPdpItemRepository.saveAndFlush( calculateActionPdpItemEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                        CalculateLogEntity.builder().type("calculate_action_pdp_item").content(JSONObject.toJSONString(trackingEventActionEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculateActionPdpItemEntity calculateActionPdpItemEntityNew = CalculateActionPdpItemEntity.builder()
                    .date( currentTime )
                    .hour( hour )
                    .campaignName(trackingEventActionEntity.getCampaign())
                    .merchantId( trackingEventActionEntity.getMerchantId() )
                    .storeId( trackingEventActionEntity.getStoreId() )
                    .itemId(Integer.parseInt(itemId) )
                    .itemName(valueJson.getString("itemName"))
                    .itemCode(valueJson.getString("itemCode"))
                    .imageSrc(valueJson.getString("imageSrc"))
                    .pvCount( 1 )
                    .uvCount( 1 )
                    .itemAmount(itemAmount)
                    .itemOrderCount(itemOrderCount)
                    .build();
            CalculateActionPdpItemEntity isSave = calculateActionPdpItemRepository.saveAndFlush( calculateActionPdpItemEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                        CalculateLogEntity.builder().type("calculate_action_pdp_item").content(JSONObject.toJSONString(trackingEventActionEntity)).createTime( currentTime ).build()
                );
            }
        }
        return true;
    }

    /**
     * Action分享统计
     * @param trackingEventActionEntity
     * @return
     */
    public Boolean receiveCalculateActionShare(TrackingEventActionEntity trackingEventActionEntity){
        Date currentTime = trackingEventActionEntity.getCreateTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentTime );
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer union = findUniqueId( trackingEventActionEntity, currentTime );

        CalculateActionShareEntity calculateActionShareEntity = calculateActionShareRepository.findByDateAndHourAndMerchantIdAndStoreIdAndShare(
                currentTime,hour, trackingEventActionEntity.getMerchantId(),
                trackingEventActionEntity.getStoreId(),trackingEventActionEntity.getEventMessage()
        );

        if (calculateActionShareEntity != null) {
            CalculateActionShareEntity calculateActionShareEntityExists = CalculateActionShareEntity.builder()
                    .shareId( calculateActionShareEntity.getShareId() )
                    .date( currentTime )
                    .hour( hour )
                    .merchantId( calculateActionShareEntity.getMerchantId() )
                    .storeId( calculateActionShareEntity.getStoreId() )
                    .pvCount( calculateActionShareEntity.getPvCount() + 1)
                    .uvCount( calculateActionShareEntity.getUvCount() + union )
                    .share( calculateActionShareEntity.getShare() ).build();
            CalculateActionShareEntity isSave = calculateActionShareRepository.saveAndFlush( calculateActionShareEntityExists );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_action_share").content(JSONObject.toJSONString(trackingEventActionEntity)).createTime( currentTime ).build()
                );
            }
        }else{
            CalculateActionShareEntity calculateActionShareEntityNew = CalculateActionShareEntity.builder()
                    .date( currentTime )
                    .hour( hour )
                    .merchantId( trackingEventActionEntity.getMerchantId() )
                    .storeId( trackingEventActionEntity.getStoreId() )
                    .pvCount( 1)
                    .uvCount( 1 )
                    .share( trackingEventActionEntity.getEventMessage() ).build();
            CalculateActionShareEntity isSave = calculateActionShareRepository.saveAndFlush( calculateActionShareEntityNew );
            if( isSave == null ){
                calculateLogRepository.saveAndFlush(
                    CalculateLogEntity.builder().type("calculate_action_share").content(JSONObject.toJSONString(trackingEventActionEntity)).createTime( currentTime ).build()
                );
            }
        }
        return true;
    }

    private Integer findUniqueId(TrackingEventActionEntity trackingEventActionEntity, Date date){
        ElasticComponent.SearchDocumentResponse trackingEventActionEntitySdr = elasticsearchService.findByIndexByUniqueIdAndMerchantIdAndStoreIdAndCreateDate(TRACKING_ACTION_INDEX,trackingEventActionEntity.getUniqueId(),
                trackingEventActionEntity.getMerchantId(), trackingEventActionEntity.getStoreId(), date );
        Integer union = 1;
        if( trackingEventActionEntitySdr.getHits().getTotal() > 1 ){
            union = 0;
        }
        return union;
    }
}
