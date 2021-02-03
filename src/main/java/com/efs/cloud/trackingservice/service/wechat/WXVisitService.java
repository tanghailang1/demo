package com.efs.cloud.trackingservice.service.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.CloudStoreConfigOutputDTO;
import com.efs.cloud.trackingservice.dto.wechat.WXDateInputDTO;
import com.efs.cloud.trackingservice.entity.wechat.*;
import com.efs.cloud.trackingservice.repository.wechat.*;
import com.efs.cloud.trackingservice.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jabez.huang
 */
@Service
@Slf4j
public class WXVisitService {

    @Value("${wechat.data.url}")
    private String wechatDataBaseUrl;
    @Value("${cloud_url}")
    private String cloudUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private Integer maxDay = 3;
    @Autowired
    private WXCloudService wxCloudService;
    @Autowired
    private WXVisitPageRepository wxVisitPageRepository;
    @Autowired
    private WXVisitDistributionSourceRepository wxVisitDistributionSourceRepository;
    @Autowired
    private WXVisitDistributionStaytimeRepository wxVisitDistributionStaytimeRepository;
    @Autowired
    private WXVisitDistributionDepthRepository wxVisitDistributionDepthRepository;
    @Autowired
    private WXVisitDistributionInfoRepository wxVisitDistributionInfoRepository;
    private HashMap<String,String> distributionInfoMap = new HashMap<>();


    private JSONArray getVisitData(String url, String beginDate, String endDate){
        JSONObject visitObject = wxCloudService.getStringData(url, beginDate, endDate);
        JSONArray listData = JSONArray.parseArray( JSONObject.toJSONString(visitObject.get("list")) );
        return listData;
}

    public ServiceResult getVisitPage(WXDateInputDTO wxDateInputDTO) throws ParseException {
        List<String> days = DateUtil.getDays(wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate());
        if( days.size() > maxDay ){
            return ServiceResult.builder().code(-1001).msg("Max Day Less Than 7 days").data(null).build();
        }

        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Map<String, List<WXVisitPageEntity>> dataMap = new HashMap<>();
        for( String day : days ) {
            Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
            Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
            List<WXVisitPageEntity> wxVisitPageEntity = wxVisitPageRepository.findByMerchantIdAndStoreIdAndRefDate(merchantId, storeId, DateUtil.stringToDate(day, "yyyy-MM-dd"));
            if (wxVisitPageEntity.size() == 0) {
                String url = wechatDataBaseUrl + "/getweanalysisappidvisitpage?access_token="+wxCloudService.getToken(wxDateInputDTO.getAppId());
                JSONArray jsonArray = getVisitData( url, day, day );
                if( jsonArray == null ){
                    dataMap.put( day, null );
                }else{
                    for(int i = 0; i < jsonArray.size(); i++ ){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        WXVisitPageEntity wxVisitPageEntityNew = WXVisitPageEntity.builder()
                                .merchantId( merchantId )
                                .storeId( storeId )
                                .refDate( DateUtil.stringToDate(day, "yyyy-MM-dd") )
                                .pagePath(jsonObject.getString("page_path"))
                                .pageVisitPv(Integer.valueOf(jsonObject.getString("page_visit_pv")))
                                .pageVisitUv(Integer.valueOf(jsonObject.getString("page_visit_uv")))
                                .pageStaytimePv(Double.valueOf(jsonObject.getString("page_staytime_pv")))
                                .entrypagePv(Integer.valueOf(jsonObject.getString("entrypage_pv")))
                                .exitpagePv(Integer.valueOf(jsonObject.getString("exitpage_pv")))
                                .pageSharePv(Integer.valueOf(jsonObject.getString("page_share_pv")))
                                .pageShareUv(Integer.valueOf(jsonObject.getString("page_share_uv")))
                                .updateTime( new Date() ).build();
                        wxVisitPageRepository.save( wxVisitPageEntityNew );

                    }
                    dataMap.put( day, wxVisitPageRepository.findByMerchantIdAndStoreIdAndRefDate(merchantId, storeId, DateUtil.stringToDate(day, "yyyy-MM-dd")) );
                }
            }else{
                dataMap.put( day, wxVisitPageEntity );
            }
        }
        return ServiceResult.builder().code(200).msg("Success").data( dataMap ).build();
    }

    public ServiceResult getVisitSource(WXDateInputDTO wxDateInputDTO) throws ParseException {
        List<String> days = DateUtil.getDays(wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate());
        if( days.size() > maxDay ){
            return ServiceResult.builder().code(-1001).msg("Max Day Less Than 3 days").data(null).build();
        }

        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Map<String, List<WXVisitDistributionSourceEntity>> dataMap = new HashMap<>();
        for( String day : days ) {
            Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
            Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
            List<WXVisitDistributionSourceEntity> wxVisitDistributionSourceEntityList = wxVisitDistributionSourceRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, DateUtil.stringToDate(day, "yyyy-MM-dd") );
            if( wxVisitDistributionSourceEntityList.size() > 0 ){
                dataMap.put( day, wxVisitDistributionSourceEntityList );
            }else{
                Boolean isSync = syncVisitData(day,wxDateInputDTO.getAppId(), merchantId, storeId, "source");
                if( isSync ){
                    dataMap.put( day, wxVisitDistributionSourceRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, DateUtil.stringToDate(day, "yyyy-MM-dd") ));
                }else{
                    dataMap.put( day, null );
                }
            }
        }

        return ServiceResult.builder().code(200).msg("Success").data( dataMap ).build();
    }

    public ServiceResult getVisitStayTime(WXDateInputDTO wxDateInputDTO) throws ParseException {
        List<String> days = DateUtil.getDays(wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate());
        if( days.size() > maxDay ){
            return ServiceResult.builder().code(-1001).msg("Max Day Less Than 3 days").data(null).build();
        }

        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Map<String, List<WXVisitDistributionStaytimeEntity>> dataMap = new HashMap<>();
        for( String day : days ) {
            Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
            Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
            List<WXVisitDistributionStaytimeEntity> wxVisitDistributionSourceEntityList = wxVisitDistributionStaytimeRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, DateUtil.stringToDate(day, "yyyy-MM-dd") );
            if( wxVisitDistributionSourceEntityList.size() > 0 ){
                dataMap.put( day, wxVisitDistributionSourceEntityList );
            }else{
                Boolean isSync = syncVisitData(day,wxDateInputDTO.getAppId(), merchantId, storeId, "source");
                if( isSync ){
                    dataMap.put( day, wxVisitDistributionStaytimeRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, DateUtil.stringToDate(day, "yyyy-MM-dd") ));
                }else{
                    dataMap.put( day, null );
                }
            }
        }

        return ServiceResult.builder().code(200).msg("Success").data( dataMap ).build();
    }

    public ServiceResult getVisitDepth(WXDateInputDTO wxDateInputDTO) throws ParseException {
        List<String> days = DateUtil.getDays(wxDateInputDTO.getBeginDate(), wxDateInputDTO.getEndDate());
        if( days.size() > maxDay ){
            return ServiceResult.builder().code(-1001).msg("Max Day Less Than 3 days").data(null).build();
        }

        CloudStoreConfigOutputDTO cloudStoreConfigOutputDTO = wxCloudService.getMerchant(wxDateInputDTO.getAppId());
        if( cloudStoreConfigOutputDTO == null ){
            return ServiceResult.builder().code(-1002).msg("error app").data(null).build();
        }

        Map<String, List<WXVisitDistributionDepthEntity>> dataMap = new HashMap<>();
        for( String day : days ) {
            Integer merchantId = cloudStoreConfigOutputDTO.getCloudMerchantId();
            Integer storeId = cloudStoreConfigOutputDTO.getCloudMerchantStoreId();
            List<WXVisitDistributionDepthEntity> wxVisitDistributionDepthEntityList = wxVisitDistributionDepthRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, DateUtil.stringToDate(day, "yyyy-MM-dd") );
            if( wxVisitDistributionDepthEntityList.size() > 0 ){
                dataMap.put( day, wxVisitDistributionDepthEntityList );
            }else{
                Boolean isSync = syncVisitData(day,wxDateInputDTO.getAppId(), merchantId, storeId, "source");
                if( isSync ){
                    dataMap.put( day, wxVisitDistributionDepthRepository.findByMerchantIdAndStoreIdAndRefDate( merchantId, storeId, DateUtil.stringToDate(day, "yyyy-MM-dd") ));
                }else{
                    dataMap.put( day, null );
                }
            }
        }

        return ServiceResult.builder().code(200).msg("Success").data( dataMap ).build();
    }

    private HashMap<String,String> findDistributionInfo(){
        if( distributionInfoMap.size() == 0 ){
            List<WXVisitDistributionInfoEntity> wxVisitDistributionInfoEntityList = wxVisitDistributionInfoRepository.findAll();
            for( WXVisitDistributionInfoEntity wxVisitDistributionInfoEntity : wxVisitDistributionInfoEntityList ){
                distributionInfoMap.put(wxVisitDistributionInfoEntity.getDistributionType()+"_"+wxVisitDistributionInfoEntity.getItemKey(),wxVisitDistributionInfoEntity.getItemName());
            }
        }
        return distributionInfoMap;
    }

    public Boolean syncVisitData(String day, String appId,
                                 Integer merchantId, Integer storeId, String type) throws ParseException {
        String url = wechatDataBaseUrl + "/getweanalysisappidvisitdistribution?access_token="+wxCloudService.getToken(appId);
        JSONArray jsonArray = getVisitData( url, day, day );
        if( jsonArray == null ){
            return false;
        }else {
            HashMap<String,String> distributionInfo = findDistributionInfo();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String index = (String) jsonObject.get("index");
                JSONArray listData = JSONArray.parseArray( JSONObject.toJSONString(jsonObject.get("item_list")) );
                for (int j = 0; j < listData.size(); j++) {
                    JSONObject listObject = listData.getJSONObject(j);
                    if( index.equals("access_source_session_cnt") ){
                        WXVisitDistributionSourceEntity wxVisitDistributionSourceEntity = WXVisitDistributionSourceEntity.builder()
                                .merchantId( merchantId )
                                .storeId( storeId )
                                .refDate( DateUtil.stringToDate(day, "yyyy-MM-dd") )
                                .accessKey( (Integer) listObject.get("key") )
                                .accessName( distributionInfo.get("access_source_session_cnt_"+listObject.get("key") ) )
                                .accessValue( Integer.valueOf((Integer) listObject.get("value")) )
                                .updateTime( new Date() ).build();
                        wxVisitDistributionSourceRepository.save( wxVisitDistributionSourceEntity );
                    }else if( index.equals("access_staytime_info") ){
                        WXVisitDistributionStaytimeEntity wxVisitDistributionStaytimeEntity = WXVisitDistributionStaytimeEntity.builder()
                                .merchantId( merchantId )
                                .storeId( storeId )
                                .refDate( DateUtil.stringToDate(day, "yyyy-MM-dd") )
                                .accessKey( (Integer) listObject.get("key") )
                                .accessName( distributionInfo.get("access_staytime_info_"+listObject.get("key") ) )
                                .accessValue( Integer.valueOf((Integer) listObject.get("value")) )
                                .updateTime( new Date() ).build();
                        wxVisitDistributionStaytimeRepository.save( wxVisitDistributionStaytimeEntity );
                    }else if( index.equals("access_depth_info") ){
                        WXVisitDistributionDepthEntity wxVisitDistributionDepthEntity = WXVisitDistributionDepthEntity.builder()
                                .merchantId( merchantId )
                                .storeId( storeId )
                                .refDate( DateUtil.stringToDate(day, "yyyy-MM-dd") )
                                .accessKey( (Integer) listObject.get("key") )
                                .accessName( distributionInfo.get("access_depth_info_"+listObject.get("key") ) )
                                .accessValue( Integer.valueOf((Integer) listObject.get("value")) )
                                .updateTime( new Date() ).build();
                        wxVisitDistributionDepthRepository.save( wxVisitDistributionDepthEntity );
                    }
                }
            }
        }
        return true;
    }


}
