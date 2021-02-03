package com.efs.cloud.trackingservice.service.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.CloudStoreConfigOutputDTO;
import com.efs.cloud.trackingservice.dto.ResponseOutputDTO;
import com.efs.cloud.trackingservice.dto.wechat.WXDateInputDTO;
import com.efs.cloud.trackingservice.dto.wechat.WXDateOutputDTO;
import com.efs.cloud.trackingservice.entity.wechat.WXDailyRetainEntity;
import com.efs.cloud.trackingservice.entity.wechat.WXMonthlyRetainEntity;
import com.efs.cloud.trackingservice.entity.wechat.WXWeeklyRetainEntity;
import com.efs.cloud.trackingservice.repository.wechat.WXDailyRetainRepository;
import com.efs.cloud.trackingservice.repository.wechat.WXMonthlyRetainRepository;
import com.efs.cloud.trackingservice.repository.wechat.WXWeeklyRetainRepository;
import com.efs.cloud.trackingservice.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.*;

/**
 * @author jabez.huang
 */
@Slf4j
@Service
public class WXRetainService {

    @Value("${wechat.data.url}")
    private String wechatDataBaseUrl;
    @Value("${cloud_url}")
    private String cloudUrl;
    private RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private WXDailyRetainRepository wxDailyRetainRepository;
    @Autowired
    private WXMonthlyRetainRepository wxMonthlyRetainRepository;
    @Autowired
    private WXWeeklyRetainRepository wxWeeklyRetainRepository;
    @Autowired
    private WXCloudService wxCloudService;
    private Integer maxDay = 30;

    private HashMap<String,String> getRetainData(String url, String beginDate, String endDate){
        JSONObject retainObject = wxCloudService.getStringData(url, beginDate, endDate);
        JSONArray uvNewData = JSONArray.parseArray( JSONObject.toJSONString(retainObject.get("visit_uv_new")) );
        JSONArray uvData = JSONArray.parseArray( JSONObject.toJSONString(retainObject.get("visit_uv")) );
        HashMap<String,String> hashMap = new HashMap<>();

        if( uvNewData.size() == 0 ){
            return null;
        }

        for(int i = 0; i < uvNewData.size(); i++ ){
            JSONObject uvNewObject = uvNewData.getJSONObject(i);
            JSONObject uvObject = uvData.getJSONObject(i);
            if( (int)uvNewObject.get("key") == 0 ){
                hashMap.put("newUv", String.valueOf(uvNewObject.get("value")));
            }
            if( (int)uvObject.get("key") == 0 ){
                hashMap.put("uv", String.valueOf(uvObject.get("value")));
            }
        }
        hashMap.put("newContent", String.valueOf(retainObject.get("visit_uv_new")));
        hashMap.put("content", String.valueOf(retainObject.get("visit_uv")));
        return hashMap;
    }

    public ServiceResult getDailyRetain(WXDateInputDTO wxDateInputDTO) throws ParseException {
        List<String> days = DateUtil.getDays(wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate());
        if( days.size() > maxDay ){
            return ServiceResult.builder().code(-1001).msg("Max Day Less Than 30 days").data(null).build();
        }

        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Map<String,WXDailyRetainEntity> dataMap = new HashMap<>();
        for( String day : days ){
            Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
            Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
            WXDailyRetainEntity wxDailyRetainEntity = wxDailyRetainRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, DateUtil.stringToDate(day, "yyyy-MM-dd") );
            if( wxDailyRetainEntity == null ){
                String url = wechatDataBaseUrl + "/getweanalysisappiddailyretaininfo?access_token="+wxCloudService.getToken(wxDateInputDTO.getAppId());
                HashMap<String,String> responseEntity = getRetainData( url, day, day );
                if( responseEntity == null ){
                    dataMap.put( day, null );
                }else{
                    WXDailyRetainEntity wxDailyRetainEntityNew = WXDailyRetainEntity.builder()
                            .merchantId( merchantId )
                            .storeId( storeId )
                            .refDate( DateUtil.stringToDate(day, "yyyyMMdd") )
                            .visitUvNew( Integer.valueOf(responseEntity.get("newUv")) )
                            .visitUv( Integer.valueOf(responseEntity.get("uv")) )
                            .content( responseEntity.get("content") )
                            .contentNew( responseEntity.get("newContent") )
                            .updateTime( new Date() ).build();
                    WXDailyRetainEntity WXDailyRetainEntityExists = wxDailyRetainRepository.saveAndFlush( wxDailyRetainEntityNew );
                    dataMap.put( day, WXDailyRetainEntityExists );
                }
            }else{
                dataMap.put( day, wxDailyRetainEntity );
            }
        }
        return ServiceResult.builder().code(200).msg("Success").data( dataMap ).build();
    }

    public ServiceResult getMonthlyRetain(WXDateInputDTO wxDateInputDTO){
        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
        Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
        WXMonthlyRetainEntity wxMonthlyRetainEntity = wxMonthlyRetainRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, wxDateInputDTO.getBeginDate() );
        if( wxMonthlyRetainEntity == null ){
            String url = wechatDataBaseUrl + "/getweanalysisappidmonthlyretaininfo?access_token="+wxCloudService.getToken(wxDateInputDTO.getAppId());
            HashMap<String,String> responseEntity = getRetainData( url, wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate() );
            if( responseEntity != null ){
                WXMonthlyRetainEntity wxMonthlyRetainEntityNew = WXMonthlyRetainEntity.builder()
                        .merchantId(merchantId)
                        .storeId(storeId)
                        .refDate(wxDateInputDTO.getBeginDate())
                        .visitUvNew(Integer.valueOf(responseEntity.get("newUv")))
                        .visitUv(Integer.valueOf(responseEntity.get("uv")))
                        .updateTime(new Date()).build();
                wxMonthlyRetainEntity = wxMonthlyRetainRepository.saveAndFlush(wxMonthlyRetainEntityNew);
            }
        }
        return ServiceResult.builder().code(200).msg("Success").data( wxMonthlyRetainEntity ).build();

    }

    public ServiceResult getWeeklyRetain(WXDateInputDTO wxDateInputDTO){
        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
        Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
        WXWeeklyRetainEntity wxWeeklyRetainEntity = wxWeeklyRetainRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, wxDateInputDTO.getBeginDate() );
        if( wxWeeklyRetainEntity == null ){
            String url = wechatDataBaseUrl + "/getweanalysisappidweeklyretaininfo?access_token="+wxCloudService.getToken(wxDateInputDTO.getAppId());
            HashMap<String,String> responseEntity = getRetainData( url, wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate() );
            if( responseEntity != null ) {
                WXWeeklyRetainEntity wxWeeklyRetainEntityNew = WXWeeklyRetainEntity.builder()
                        .merchantId(merchantId)
                        .storeId(storeId)
                        .refDate(wxDateInputDTO.getBeginDate())
                        .visitUvNew(Integer.valueOf(responseEntity.get("newUv")))
                        .visitUv(Integer.valueOf(responseEntity.get("uv")))
                        .content(responseEntity.get("content"))
                        .contentNew(responseEntity.get("newContent"))
                        .updateTime(new Date()).build();
                wxWeeklyRetainEntity = wxWeeklyRetainRepository.saveAndFlush(wxWeeklyRetainEntityNew);
            }
        }
        return ServiceResult.builder().code(200).msg("Success").data( wxWeeklyRetainEntity ).build();

    }
}
