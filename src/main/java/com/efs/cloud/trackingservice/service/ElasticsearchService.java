package com.efs.cloud.trackingservice.service;

import com.efs.cloud.trackingservice.component.ElasticComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: maxun
 * @Date: 2020/11/26
 */
@Service
@Slf4j
public class ElasticsearchService {

    @Autowired
    private ElasticComponent elasticComponent;

    public ElasticComponent.SearchDocumentResponse findByIndexByUniqueIdAndMerchantIdAndStoreIdAndCreateDate(String index, String uniqueId, Integer merchantId, Integer storeId, Date createDate,String field,String fieldValue) {
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        stringBuilder.append("{");
        stringBuilder.append("\"query\":{\"bool\":{\"must\":[");
        stringBuilder.append("{\"term\":{\"merchantId\":\"").append(merchantId).append("\"}},");
        stringBuilder.append("{\"term\":{\"storeId\":\"").append(storeId).append("\"}},");
        switch (field){
            case "scene":
                stringBuilder.append("{\"term\":{\"scene\":\"").append(fieldValue).append("\"}},");
                break;
            case "action":
                stringBuilder.append("{\"term\":{\"action.keyword\":\"").append(fieldValue).append("\"}},");
                break;
            case "path":
                stringBuilder.append("{\"term\":{\"path.keyword\":\"").append(fieldValue).append("\"}},");
                break;
            case "eventType":
                stringBuilder.append("{\"term\":{\"eventType.keyword\":\"").append(fieldValue).append("\"}},");
                break;
            case "eventValue":
                stringBuilder.append("{\"term\":{\"eventValue.keyword\":\"").append(fieldValue).append("\"}},");
                break;
            case "eventMessage":
                stringBuilder.append("{\"term\":{\"eventMessage.keyword\":\"").append(fieldValue).append("\"}},");
                break;
            default:
                break;
        }
        stringBuilder.append("{\"term\":{\"uniqueId.keyword\":\"").append(uniqueId).append("\"}},");
        stringBuilder.append("{\"term\":{\"createDate\":\"").append(sdf.format(createDate)).append("\"}}");
        stringBuilder.append("]}}}");
        String body = stringBuilder.toString();
        //log.info("ES body:" + body);
        ElasticComponent.SearchDocumentResponse searchDocumentResponse = elasticComponent.searchDocument(index, body);
        return searchDocumentResponse;
    }

    public ElasticComponent.SearchDocumentResponse findByIndexByCreateDateAndMerchantIdAndStoreIdAndCustomerId(String index, Date createDate, Integer merchantId, Integer storeId, Integer customerId,String field,String fieldValue) {
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        stringBuilder.append("{");
        stringBuilder.append("\"query\":{\"bool\":{\"must\":[");
        stringBuilder.append("{\"term\":{\"merchantId\":\"").append(merchantId).append("\"}},");
        stringBuilder.append("{\"term\":{\"storeId\":\"").append(storeId).append("\"}},");
        switch (field){
            case "itemId":
                stringBuilder.append("{\"term\":{\"itemId\":\"").append(fieldValue).append("\"}},");
                break;
            case "skuCode":
                stringBuilder.append("{\"term\":{\"skuCode.keyword\":\"").append(fieldValue).append("\"}},");
                break;
            case "categoryId":
                stringBuilder.append("{\"term\":{\"categoryId\":\"").append(fieldValue).append("\"}},");
                break;
            case "scene":
                stringBuilder.append("{\"term\":{\"scene\":\"").append(fieldValue).append("\"}},");
                break;
            default:
                break;
        }
        stringBuilder.append("{\"term\":{\"customerId\":\"").append(customerId).append("\"}},");
        stringBuilder.append("{\"term\":{\"createDate\":\"").append(sdf.format(createDate)).append("\"}}");
        stringBuilder.append("]}}}");
        String body = stringBuilder.toString();
        ElasticComponent.SearchDocumentResponse searchDocumentResponse = elasticComponent.searchDocument(index, body);
        return searchDocumentResponse;
    }

    public ElasticComponent.SearchDocumentResponse findByItemIdAndMerchantIdAndStoreIdAndCreateDate(String index,Integer itemId, Integer merchantId, Integer storeId, Date createDate){
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        stringBuilder.append("{");
        stringBuilder.append("\"query\":{\"bool\":{\"must\":[");
        stringBuilder.append("{\"term\":{\"merchantId\":\"").append(merchantId).append("\"}},");
        stringBuilder.append("{\"term\":{\"storeId\":\"").append(storeId).append("\"}},");
        stringBuilder.append("{\"term\":{\"orderItems.itemId\":\"").append(itemId).append("\"}},");
        stringBuilder.append("{\"term\":{\"createDate\":\"").append(sdf.format(createDate)).append("\"}}");
        stringBuilder.append("]}}}");
        String body = stringBuilder.toString();
        ElasticComponent.SearchDocumentResponse searchDocumentResponse = elasticComponent.searchDocument(index, body);
        return searchDocumentResponse;

    }

    public ElasticComponent.SearchDocumentResponse findByCategoryIdAndMerchantIdAndStoreIdAndCreateDate(String index,Integer categoryId, Integer merchantId, Integer storeId, Date createDate){
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        stringBuilder.append("{");
        stringBuilder.append("\"query\":{\"bool\":{\"must\":[");
        stringBuilder.append("{\"term\":{\"merchantId\":\"").append(merchantId).append("\"}},");
        stringBuilder.append("{\"term\":{\"storeId\":\"").append(storeId).append("\"}},");
        stringBuilder.append("{\"term\":{\"orderItems.categoryId\":\"").append(categoryId).append("\"}},");
        stringBuilder.append("{\"term\":{\"createDate\":\"").append(sdf.format(createDate)).append("\"}}");
        stringBuilder.append("]}}}");
        String body = stringBuilder.toString();
        ElasticComponent.SearchDocumentResponse searchDocumentResponse = elasticComponent.searchDocument(index, body);
        return searchDocumentResponse;
    }

    public ElasticComponent.SearchDocumentResponse findByIndexAndCustomerIdAndStatus(String index, Integer customerId){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append("\"query\":{\"bool\":{\"must\":[");
        stringBuilder.append("{\"term\":{\"customerId\":\"").append(customerId).append("\"}},");
        stringBuilder.append("{\"terms\":{\"status.keyword\":[").append("\"TRADE_FINISHED\",\"WAIT_SELLER_SEND_GOODS\",\"WAIT_BUYER_CONFIRM_GOODS\",\"WAIT_BUYER_PAY\"").append("]}}");
        stringBuilder.append("]}}}");
        String body = stringBuilder.toString();
        ElasticComponent.SearchDocumentResponse searchDocumentResponse = elasticComponent.searchDocument(index, body);
        return searchDocumentResponse;
    }
}
