package com.efs.cloud.trackingservice.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.TrackingOrderInputDTO;
import com.efs.cloud.trackingservice.entity.data.DataMerchantTokenEntity;
import com.efs.cloud.trackingservice.entity.entity.OrderDTOEntity;
import com.efs.cloud.trackingservice.entity.entity.OrderItemDTOEntity;
import com.efs.cloud.trackingservice.entity.tracking.TrackingEventOrderEntity;
import com.efs.cloud.trackingservice.enums.OrderStatusEnum;
import com.efs.cloud.trackingservice.repository.data.DataMerchantTokenRepository;
import com.efs.cloud.trackingservice.repository.tracking.TrackingEventOrderRepository;
import com.efs.cloud.trackingservice.service.tracking.TrackingOrderService;
import com.efs.cloud.trackingservice.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: maxun
 */
@Slf4j
@Service
@EnableScheduling
public class OrderComparisonService {

    @Value(value = "${cloud_url}")
    private String cloudUrl;
    @Value(value = "${is_order_comparison_daily}")
    private Boolean isOrderComparisonDaily;
    @Autowired
    private DataMerchantTokenRepository dataMerchantTokenRepository;
    @Autowired
    private TrackingEventOrderRepository trackingEventOrderRepository;
    @Autowired
    private TrackingOrderService trackingOrderService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void OrderComparisonDaily(){
        try {
            if (isOrderComparisonDaily) {
                String createTime = DateUtil.getSpecifiedDayBefore(DateUtil.getDateToString(new Date(), ""), "yyyy-MM-dd", 1);
                List<DataMerchantTokenEntity> tokenEntityList = dataMerchantTokenRepository.findAll();
                for (DataMerchantTokenEntity dataMerchantTokenEntity : tokenEntityList) {
                    this.OrderComparison(dataMerchantTokenEntity.getMerchantId(), createTime);
                }
            }
        }catch (Exception e){
            log.error("OrderComparisonDaily:" + e);
        }
    }

    @Async
    public ServiceResult OrderComparison(Integer merchantId, String dateTime){
        String createTimeBegin = dateTime + " 00:00:00";
        String createTimeEnd = dateTime + " 23:59:59";
        DataMerchantTokenEntity dataMerchantTokenEntity = dataMerchantTokenRepository.findAllByMerchantId(merchantId);
        String token = dataMerchantTokenEntity.getToken();
        Integer orderTotal = this.getCloudOrderData(token,merchantId,null,10,createTimeBegin,createTimeEnd).getInteger("total");
        JSONObject data = this.getCloudOrderData(token,merchantId,null,orderTotal,createTimeBegin,createTimeEnd);
        JSONArray content = data.getJSONArray("content");
        Integer size = content.size();
        log.info("-----start OrderComparison:" + merchantId + ",date:" + dateTime);
        for (int i = 0;i < size;i++){
            if ("tmall".equals(content.getJSONObject(i).getString("orderSource"))){
                continue;
            }
            JSONObject contentObj = content.getJSONObject(i);
            String orderId = contentObj.get("orderId").toString();
            Integer storeId = contentObj.getInteger("storeId");
            String mobile = contentObj.getString("mobile");
            String status = contentObj.getString("status");
            Integer customerId = contentObj.getInteger("customerId");
            BigDecimal payment = contentObj.getBigDecimal("payment");
            BigDecimal discountFee = contentObj.getBigDecimal("discountFee");
            BigDecimal postFee = contentObj.getBigDecimal("postFee");
            BigDecimal itemTotal = contentObj.getBigDecimal("itemTotal");
            Date createTime = contentObj.getDate("createTime");
            Date payTime = contentObj.getDate("payTime");
            List<OrderItemDTOEntity> orderItemDTOEntityList = new ArrayList<>();
            JSONArray items = contentObj.getJSONArray("items");
            for (int j = 0;j < items.size();j++){
                JSONObject itemObj = items.getJSONObject(j);
                OrderItemDTOEntity orderItemDTOEntity = new OrderItemDTOEntity();
                String itemName = itemObj.getString("itemName");
                Integer itemId = itemObj.getInteger("itemId");
                Integer qty = itemObj.getInteger("qty");
                BigDecimal price = itemObj.getBigDecimal("price");
                BigDecimal itemDiscountFee = itemObj.getBigDecimal("discountFee");
                orderItemDTOEntity.setCategoryId(1);
                orderItemDTOEntity.setCategoryName("");
                orderItemDTOEntity.setDiscountAmount(itemDiscountFee.multiply(new BigDecimal(100)).intValue());
                orderItemDTOEntity.setItemId(itemId);
                orderItemDTOEntity.setItemName(itemName);
                orderItemDTOEntity.setOrderQty(qty);
                orderItemDTOEntity.setRowTotal(price.multiply(new BigDecimal(qty)).multiply(new BigDecimal(100)).intValue());
                orderItemDTOEntity.setUnitPrice(price.multiply(new BigDecimal(100)).intValue());
                orderItemDTOEntityList.add(orderItemDTOEntity);
            }
            TrackingOrderInputDTO trackingOrderInputDTO = new TrackingOrderInputDTO();
            trackingOrderInputDTO.setCampaign("");
            trackingOrderInputDTO.setIp("0.0.0.0");
            trackingOrderInputDTO.setUniqueId("");
            trackingOrderInputDTO.setCustomerId(customerId);
            trackingOrderInputDTO.setMerchantId(merchantId);
            trackingOrderInputDTO.setStoreId(storeId);
            trackingOrderInputDTO.setScene(1001);
            trackingOrderInputDTO.setOrderId(orderId);
            trackingOrderInputDTO.setOrderAmount(payment.multiply(new BigDecimal(100)).intValue());
            trackingOrderInputDTO.setOrderDiscountAmount(discountFee.multiply(new BigDecimal(100)).intValue());
            trackingOrderInputDTO.setOrderShippingFee(postFee.multiply(new BigDecimal(100)).intValue());
            trackingOrderInputDTO.setOrderSubtotal(itemTotal.multiply(new BigDecimal(100)).intValue());
            trackingOrderInputDTO.setData(new HashMap());
            trackingOrderInputDTO.setOrderItems(orderItemDTOEntityList);
            String jwt = this.getCloudJwt(mobile,merchantId);
            if ("WAIT_BUYER_PAY".equals(status) || "TRADE_CLOSED".equals(status) || "TRADE_CLOSED_BY_CLOUD".equals(status)){
                TrackingEventOrderEntity trackingEventOrderEntity = trackingEventOrderRepository.findAllByOrderIdAndStatus(orderId,"ORDER_PAY");
                if (trackingEventOrderEntity == null){
                    trackingOrderService.receiveEventOrder(OrderDTOEntity.builder()
                            .time(createTime)
                            .jwt(jwt)
                            .orderStatus(OrderStatusEnum.ORDER_PAY.getValue())
                            .trackingOrderInputDTO(trackingOrderInputDTO).build());
                    continue;
                }
            }
            if ("WAIT_SELLER_SEND_GOODS".equals(status) || "WAIT_BUYER_CONFIRM_GOODS".equals(status) || "TRADE_FINISHED".equals(status)){
                TrackingEventOrderEntity trackingEventOrderEntity = trackingEventOrderRepository.findAllByOrderIdAndStatus(orderId,"ORDER_PAY");
                if (trackingEventOrderEntity == null){
                    trackingOrderService.receiveEventOrder(OrderDTOEntity.builder()
                            .time(createTime)
                            .jwt(jwt)
                            .orderStatus(OrderStatusEnum.ORDER_PAY.getValue())
                            .trackingOrderInputDTO(trackingOrderInputDTO).build());
                }
                TrackingEventOrderEntity trackingEventOrderEntityPay = trackingEventOrderRepository.findAllByOrderIdAndStatus(orderId,"PAY_SUCCESS");
                if (trackingEventOrderEntityPay == null){
                    trackingOrderService.receiveEventOrder(OrderDTOEntity.builder()
                            .time(payTime)
                            .jwt(jwt)
                            .orderStatus(OrderStatusEnum.PAY_SUCCESS.getValue())
                            .trackingOrderInputDTO(trackingOrderInputDTO).build());
                }
            }
        }
        log.info("-----end OrderComparison:" + merchantId + ",date:" + dateTime);
        return ServiceResult.builder().data("").build();
    }

    //获取cloud订单data
    public JSONObject getCloudOrderData(String token,Integer merchantId, Integer storeId,Integer pageSize,String createTimeBegin,String createTimeEnd){
        JSONObject orderData = null;
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", token);
        HttpEntity<String> strEntity = new HttpEntity<String>(headers);
        RestTemplate restTemplate = new RestTemplate();
        String cloudSearchUrl = cloudUrl + "/admin/cloud-sales-order/admin/sales_order?merchant_id=" + merchantId +
                "&create_time_begin=" + createTimeBegin +
                "&create_time_end=" + createTimeEnd +
                "&page_no=1&page_size=" + pageSize;
        if (storeId != null){
            cloudSearchUrl += "&store_id=" + storeId;
        }
        ResponseEntity<String> returnData = restTemplate.exchange(cloudSearchUrl, HttpMethod.GET, strEntity, String.class);
        JSONObject jsonDate = JSONObject.parseObject(returnData.getBody());
        if (jsonDate.getString("code").equals("1000")) {
            orderData = jsonDate.getJSONObject("data");
        }
        return orderData;
    }

    //获取jwt
    public String getCloudJwt(String mobile,Integer merchantId){
        String jwt = "";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> strEntity = new HttpEntity<String>(headers);
        RestTemplate restTemplate = new RestTemplate();
        String cloudSearchUrl = cloudUrl + "/cloud/cloud-customer-basic/jwt?merchant_id=" + merchantId +
                "&mobile=" + mobile +
                "&nick_name";
        ResponseEntity<String> returnData = restTemplate.exchange(cloudSearchUrl, HttpMethod.GET, strEntity, String.class);
        JSONObject jsonDate = JSONObject.parseObject(returnData.getBody());
        if (jsonDate.getString("code").equals("1000")) {
            jwt = jsonDate.getJSONObject("data").getString("jwt");
        }
        return jwt;
    }


}
