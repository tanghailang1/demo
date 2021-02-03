package com.efs.cloud.trackingservice.service.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.CloudStoreConfigOutputDTO;
import com.efs.cloud.trackingservice.dto.ResponseOutputDTO;
import com.efs.cloud.trackingservice.dto.wechat.WXDateOutputDTO;
import com.efs.cloud.trackingservice.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author jabez.huang
 */
@Service
@Slf4j
public class WXCloudService {

    @Value("${cloud_url}")
    private String cloudUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private Integer maxDay = 30;


    public JSONObject getStringData(String url, String beginDate, String endDate){
        HttpHeaders headers = new HttpHeaders();
        HttpMethod httpMethod = HttpMethod.POST;
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<WXDateOutputDTO> entity = new HttpEntity<>(WXDateOutputDTO.builder().begin_date(beginDate).end_date(endDate).build(),headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, httpMethod, entity,String.class);
        JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
        log.info("retain data:"+jsonObject+"-"+url+"-"+entity);
        return jsonObject;
    }

    public String getToken(String appId){
        String url = cloudUrl+"/cloud-weixin-mini/wx/access_token?app_id="+appId;
        ResponseEntity<ResponseOutputDTO> cloudStoreEntity = restTemplate.getForEntity(url, ResponseOutputDTO.class);
        HashMap<String,String> hashMap = (LinkedHashMap<String, String>) cloudStoreEntity.getBody().getData();
        return hashMap.get("accessToken");
    }

    public CloudStoreConfigOutputDTO getMerchant(String appId){
        RestTemplate restTemplate = new RestTemplate();
        String url = cloudUrl+"/cloud/website-store-service/store/miniprogram/get?appId="+appId;
        log.info("merchant url:"+url);
        ResponseEntity<ResponseOutputDTO> cloudMerchantEntity = restTemplate.getForEntity(url, ResponseOutputDTO.class);
        log.info("merchant:"+cloudMerchantEntity);
        if( cloudMerchantEntity.getBody().getData().equals("") ){
            return null;
        }
        HashMap<String,Object> hashMap = (LinkedHashMap<String, Object>) cloudMerchantEntity.getBody().getData();
        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = CloudStoreConfigOutputDTO.builder().cloudMerchantId((Integer) hashMap.get("cloudMerchantId"))
                .cloudMerchantStoreId((Integer) hashMap.get("cloudMerchantStoreId")).build();
        return cloudStoreConfigOutputDTO;
    }
}
