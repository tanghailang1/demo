package com.efs.cloud.trackingservice.service.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.CloudStoreConfigOutputDTO;
import com.efs.cloud.trackingservice.dto.ResponseOutputDTO;
import com.efs.cloud.trackingservice.dto.wechat.WXDateInputDTO;
import com.efs.cloud.trackingservice.dto.wechat.WXDateOutputDTO;
import com.efs.cloud.trackingservice.entity.wechat.WXDailySummaryEntity;
import com.efs.cloud.trackingservice.entity.wechat.WXDailyVisitTrendEntity;
import com.efs.cloud.trackingservice.entity.wechat.WXMonthlyVisitTrendEnity;
import com.efs.cloud.trackingservice.entity.wechat.WXWeeklyVisitTrendEntity;
import com.efs.cloud.trackingservice.repository.wechat.WXDailySummaryRepository;
import com.efs.cloud.trackingservice.repository.wechat.WXDailyVisitTrendRepository;
import com.efs.cloud.trackingservice.repository.wechat.WXMonthlyVisitTrendRepository;
import com.efs.cloud.trackingservice.repository.wechat.WXWeeklyVisitTrendRepository;
import com.efs.cloud.trackingservice.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jabez.huang
 */
@Service
@Slf4j
public class WXTrendService {

    @Value("${wechat.data.url}")
    private String wechatDataBaseUrl;
    @Value("${cloud_url}")
    private String cloudUrl;
    private RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private WXDailyVisitTrendRepository wxDailyVisitTrendRepository;
    @Autowired
    private WXMonthlyVisitTrendRepository wxMonthlyVisitTrendRepository;
    @Autowired
    private WXWeeklyVisitTrendRepository wxWeeklyVisitTrendRepository;
    @Autowired
    private WXDailySummaryRepository wxDailySummaryRepository;
    private Integer maxDay = 30;
    @Autowired
    private WXCloudService wxCloudService;


    private JSONObject getTrendData(String url, String beginDate, String endDate){
        JSONObject trendObject = wxCloudService.getStringData(url, beginDate, endDate);
        JSONArray listData = JSONArray.parseArray( JSONObject.toJSONString(trendObject.get("list")) );
        //log.info("list:"+listData+listData.get(0));
       JSONObject jsonObject = (JSONObject) listData.get(0);
        return jsonObject;
    }

    public ServiceResult getDailyVisitTrend(WXDateInputDTO wxDateInputDTO) throws ParseException {
        List<String> days = DateUtil.getDays(wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate());
        if( days.size() > maxDay ){
            return ServiceResult.builder().code(-1001).msg("Max Day Less Than 30 days").data(null).build();
        }

        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Map<String,WXDailyVisitTrendEntity> dataMap = new HashMap<>();
        for( String day : days ){
            Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
            Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
            WXDailyVisitTrendEntity wxDailyVisitTrendEntity = wxDailyVisitTrendRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, DateUtil.stringToDate(day, "yyyy-MM-dd") );
            if( wxDailyVisitTrendEntity == null ){
                String url = wechatDataBaseUrl + "/getweanalysisappiddailyvisittrend?access_token="+wxCloudService.getToken(wxDateInputDTO.getAppId());
                JSONObject jsonObject = getTrendData( url, day, day );
                if( jsonObject == null ){
                    dataMap.put( day, null );
                }else{
                    WXDailyVisitTrendEntity wxDailyVisitTrendEntityNew = WXDailyVisitTrendEntity.builder()
                            .merchantId( merchantId )
                            .storeId( storeId )
                            .refDate( DateUtil.stringToDate(day, "yyyyMMdd") )
                            .sessionCnt( Integer.valueOf(jsonObject.getString("session_cnt")) )
                            .visitPv( Integer.valueOf(jsonObject.getString("visit_pv")) )
                            .visitUv( Integer.valueOf(jsonObject.getString("visit_uv")) )
                            .visitUvNew( Integer.valueOf(jsonObject.getString("visit_uv_new")) )
                            .stayTimeUv( Double.valueOf(jsonObject.getString("stay_time_uv")) )
                            .stayTimeSession( Double.valueOf(jsonObject.getString("stay_time_session")) )
                            .visitDepth( Double.valueOf(jsonObject.getString("visit_depth")) )
                            .updateTime( new Date() ).build();
                    WXDailyVisitTrendEntity wxDailyVisitTrendEntityExists = wxDailyVisitTrendRepository.saveAndFlush( wxDailyVisitTrendEntityNew );
                    dataMap.put( day, wxDailyVisitTrendEntityExists );
                }
            }else{
                dataMap.put( day, wxDailyVisitTrendEntity );
            }
        }
        return ServiceResult.builder().code(200).msg("Success").data( dataMap ).build();
    }

    public ServiceResult getMonthVisitTrend(WXDateInputDTO wxDateInputDTO) throws ParseException {
        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
        Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
        WXMonthlyVisitTrendEnity wxMonthlyVisitTrendEnity = wxMonthlyVisitTrendRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, wxDateInputDTO.getBeginDate() );
        if( wxMonthlyVisitTrendEnity == null ){
            String url = wechatDataBaseUrl + "/getweanalysisappidmonthlyvisittrend?access_token="+wxCloudService.getToken(wxDateInputDTO.getAppId());
            JSONObject jsonObject = getTrendData( url, wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate() );
            if( jsonObject != null ){
                WXMonthlyVisitTrendEnity wxMonthlyVisitTrendEntityNew = WXMonthlyVisitTrendEnity.builder()
                        .merchantId( merchantId )
                        .storeId( storeId )
                        .refDate( wxDateInputDTO.getBeginDate() )
                        .sessionCnt( Integer.valueOf(jsonObject.getString("session_cnt")) )
                        .visitPv( Integer.valueOf(jsonObject.getString("visit_pv")) )
                        .visitUv( Integer.valueOf(jsonObject.getString("visit_uv")) )
                        .visitUvNew( Integer.valueOf(jsonObject.getString("visit_uv_new")) )
                        .stayTimeUv( Double.valueOf(jsonObject.getString("stay_time_uv")) )
                        .stayTimeSession( Double.valueOf(jsonObject.getString("stay_time_session")) )
                        .visitDepth( Double.valueOf(jsonObject.getString("visit_depth")) )
                        .updateTime( new Date() ).build();
                wxMonthlyVisitTrendEnity = wxMonthlyVisitTrendRepository.saveAndFlush(wxMonthlyVisitTrendEntityNew);
            }
        }
        return ServiceResult.builder().code(200).msg("Success").data( wxMonthlyVisitTrendEnity ).build();
    }

    public ServiceResult getWeeklyVisitTrend(WXDateInputDTO wxDateInputDTO){
        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
        Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
        WXWeeklyVisitTrendEntity wxWeeklyVisitTrendEntity = wxWeeklyVisitTrendRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, wxDateInputDTO.getBeginDate() );
        if( wxWeeklyVisitTrendEntity == null ){
            String url = wechatDataBaseUrl + "/getweanalysisappidweeklyvisittrend?access_token="+wxCloudService.getToken(wxDateInputDTO.getAppId());
            JSONObject jsonObject = getTrendData( url, wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate() );
            if( jsonObject != null ) {
                WXWeeklyVisitTrendEntity wxWeeklyVisitTrendEntityNew = WXWeeklyVisitTrendEntity.builder()
                        .merchantId(merchantId)
                        .storeId(storeId)
                        .refDate(wxDateInputDTO.getBeginDate())
                        .sessionCnt( Integer.valueOf(jsonObject.getString("session_cnt")) )
                        .visitPv( Integer.valueOf(jsonObject.getString("visit_pv")) )
                        .visitUv( Integer.valueOf(jsonObject.getString("visit_uv")) )
                        .visitUvNew( Integer.valueOf(jsonObject.getString("visit_uv_new")) )
                        .stayTimeUv( Double.valueOf(jsonObject.getString("stay_time_uv")) )
                        .stayTimeSession( Double.valueOf(jsonObject.getString("stay_time_session")) )
                        .visitDepth( Double.valueOf(jsonObject.getString("visit_depth")) )
                        .updateTime( new Date() ).build();
                wxWeeklyVisitTrendEntity = wxWeeklyVisitTrendRepository.saveAndFlush(wxWeeklyVisitTrendEntityNew);
            }
        }
        return ServiceResult.builder().code(200).msg("Success").data( wxWeeklyVisitTrendEntity ).build();
    }

    public ServiceResult getDailySummary(WXDateInputDTO wxDateInputDTO) throws ParseException {
        List<String> days = DateUtil.getDays(wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate());
        if( days.size() > maxDay ){
            return ServiceResult.builder().code(-1001).msg("Max Day Less Than 30 days").data(null).build();
        }

        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Map<String, WXDailySummaryEntity> dataMap = new HashMap<>();
        for( String day : days ){
            Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
            Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
            WXDailySummaryEntity wxDailySummaryEntity = wxDailySummaryRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, DateUtil.stringToDate(day, "yyyyMMdd") );
            if( wxDailySummaryEntity == null ){
                String url = wechatDataBaseUrl + "/getweanalysisappiddailysummarytrend?access_token="+wxCloudService.getToken(wxDateInputDTO.getAppId());
                JSONObject jsonObject = getTrendData( url, day, day );
                if( jsonObject == null ){
                    dataMap.put( day, null );
                }else{
                    WXDailySummaryEntity wxDailySummaryEntityNew = WXDailySummaryEntity.builder()
                            .merchantId( merchantId )
                            .storeId( storeId )
                            .refDate( DateUtil.stringToDate(day, "yyyyMMdd") )
                            .visitTotal( Integer.valueOf(jsonObject.getString("visit_total")) )
                            .sharePv( Integer.valueOf(jsonObject.getString("share_pv")) )
                            .shareUv( Integer.valueOf(jsonObject.getString("share_uv")) )
                            .updateTime( new Date() ).build();
                    WXDailySummaryEntity wxDailySummaryEntityExists = wxDailySummaryRepository.saveAndFlush( wxDailySummaryEntityNew );
                    dataMap.put( day, wxDailySummaryEntityExists );
                }
            }else{
                dataMap.put( day, wxDailySummaryEntity );
            }
        }
        return ServiceResult.builder().code(200).msg("Success").data( dataMap ).build();
    }
}
