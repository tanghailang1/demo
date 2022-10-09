package com.example.demo.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.es.SmsElasticPO;
import com.example.demo.entity.es.SmsRecordItem;
import com.example.demo.utils.CollectorExt;
import com.example.demo.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * https://blog.csdn.net/weixin_35720385/article/details/88870851
 * 同步调用和异步调用的区别
 * 还可以使用RestHighLevelClient代替restClient,但是RestHighLevelClient版本兼容不高
 */
@Service
@Slf4j
public class ElasticSearchService {
    public static final String INDEX_SMS = "matched_sms_record";//TODO 根据实际名称进行更改
    public static final String INDEX_SMS_NEW = "unified_sms_record";//TODO 根据实际名称进行更改



//    public SmsElasticPO findSmsBySameMobileOrderByTime(UserInfoMO userInfoMO){
//        SmsElasticPO smsElasticPO = findSmsByUserIdOrderByTime(userInfoMO);
//        if(CollectionUtils.isEmpty(smsElasticPO.getSms_record_item())){
//            //遍历查找同手机号的短信记录，取数量最多的那条
//            Query query = new Query(Criteria.where("mobile").is(userInfoMO.getMobile()));
//            List<UserInfoMO> userInfoMOList = this.userMongoTemplate.find(query, UserInfoMO.class);
//            //去除当前用户
//            userInfoMOList = userInfoMOList.stream().filter(i -> !i.get_id().equals(userInfoMO.get_id())).collect(Collectors.toList());
//            if(!CollectionUtils.isEmpty(userInfoMOList)){
//                //遍历其他同手机号用户，找到他们的短信，选出短信最多的
//                smsElasticPO = userInfoMOList.stream().map(i -> findSmsByUserIdOrderByTime(i)).max(Comparator.comparing(i -> i.getSms_record_item().size())).get();
//            }
//        }
//        return smsElasticPO;
//    }





    /**
     * 根据用户id找到短信记录,并按时间升序
     * @param
     * @return
     */
    public SmsElasticPO findSmsByUserIdOrderByTime(String userId){
        SmsElasticPO smsElasticPO = new SmsElasticPO();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //过滤字段
        String[] exclude = new String[0];
        String[] include = new String[]{"date", "address", "body", "createTime","type","dateString"};
        sourceBuilder.fetchSource(include, exclude);
        //查询条件
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("userId", userId);//精确查询
        sourceBuilder.query(termQueryBuilder);
        List<SmsRecordItem> matchRecords = find(sourceBuilder, INDEX_SMS, SmsRecordItem.class);//所有的短信项
        if(!CollectionUtils.isEmpty(matchRecords)){
            //按照createTime倒序
            matchRecords.sort((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()));
            Long createTime = matchRecords.get(0).getCreateTime();//拿到最近拉取短信时间
            //按照address+body+date去重
            List<SmsRecordItem> collect = matchRecords.stream().collect(CollectorExt.distinctBy(t -> t.getAddress() + t.getBody() + t.getDate()));
            collect.sort((a, b) -> Long.compare(b.getDate(), a.getDate()));//倒序
            smsElasticPO.setSms_record_item(collect);
            smsElasticPO.setCreate_time(createTime);
        }
        return smsElasticPO;
    }


    /**
     * 根据order id找到短信记录,并按adress 和 时间升序
     * @param orderId
     * @return
     */
    public SmsElasticPO findSmsByOrderIdOrderByAddressTime(String orderId){
        SmsElasticPO smsElasticPO = new SmsElasticPO();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //过滤字段
        String[] exclude = new String[0];
        String[] include = new String[]{"date", "address", "body", "createTime","type","dateString"};
        sourceBuilder.fetchSource(include, exclude);
        //查询条件
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("orderId", orderId);//精确查询
        sourceBuilder.query(termQueryBuilder);
        List<SmsRecordItem> matchRecords = find(sourceBuilder, INDEX_SMS, SmsRecordItem.class);//所有的短信项
        if(!CollectionUtils.isEmpty(matchRecords)){
            Long createTime = matchRecords.get(0).getCreateTime();//拿到最近拉取短信时间
            //按照address+body+date去重
            List<SmsRecordItem> collect = matchRecords.stream().collect(CollectorExt.distinctBy(t -> t.getAddress() + t.getBody() + t.getDateString()));
            collect = collect.stream().filter(Objects::nonNull).filter(i->i.getAddress()!=null).filter(i->i.getDate()!=null).collect(Collectors.toList());
            Comparator<SmsRecordItem> byName = Comparator.comparing(SmsRecordItem::getAddress);
            Comparator<SmsRecordItem> bySizeDesc = Comparator.comparing(SmsRecordItem::getDate);
            collect.sort(byName.thenComparing(bySizeDesc)); // 先以adress升序排列，再按照create_time升叙排列
            smsElasticPO.setSms_record_item(collect);
            smsElasticPO.setCreate_time(createTime);
        }
        return smsElasticPO;
    }

//    public static void main(String[] args) {
//        SmsElasticPO smsElasticPO = findSmsByUserIdOrderByAddressTime("6211af8a36ce5709940d8505");
//        List<SmsRecordItem> sms_record_item = smsElasticPO.getSms_record_item();
//        System.out.println(sms_record_item.size());
//    }

    /**
     * 根据user id找到短信记录,并按adress 和 时间升序
     * @param userId
     * @return
     */
    public  SmsElasticPO findSmsByUserIdOrderByAddressTime(String userId,String mobile){
        SmsElasticPO smsElasticPO = new SmsElasticPO();

        //过滤字段
        String[] exclude = new String[0];
        String[] include = new String[]{"date", "address", "body", "createTime","type","dateString"};

        TermQueryBuilder termQueryBuilderNew = QueryBuilders.termQuery("mobile", mobile);
        SearchSourceBuilder sourceBuilderNew = new SearchSourceBuilder();
        sourceBuilderNew.fetchSource(include, exclude);
        sourceBuilderNew.query(termQueryBuilderNew);
        List<SmsRecordItem> matchRecordsNew = find(sourceBuilderNew, INDEX_SMS_NEW, SmsRecordItem.class);
        if(!CollectionUtils.isEmpty(matchRecordsNew)){
            Long createTime = matchRecordsNew.get(0).getCreateTime();//拿到最近拉取短信时间
            //按照address+body+date去重
            matchRecordsNew = matchRecordsNew.stream().filter(Objects::nonNull).filter(i->i.getAddress()!=null).filter(i->i.getDate()!=null).collect(Collectors.toList());
            Comparator<SmsRecordItem> byName = Comparator.comparing(SmsRecordItem::getAddress);
            Comparator<SmsRecordItem> bySizeDesc = Comparator.comparing(SmsRecordItem::getDate);
            matchRecordsNew.sort(byName.thenComparing(bySizeDesc)); // 先以adress升序排列，再按照create_time升叙排列
            List<SmsRecordItem> collect = matchRecordsNew.stream().collect(CollectorExt.distinctBy(t -> t.getAddress() + t.getBody() + t.getDateString()));
            smsElasticPO.setSms_record_item(collect);
            smsElasticPO.setCreate_time(createTime);
            log.info("根据手机号查询到短信记录，mobile={}",mobile);
        }else {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("userId", userId);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.fetchSource(include, exclude);
            sourceBuilder.query(termQueryBuilder);
            List<SmsRecordItem> matchRecords = find(sourceBuilder, INDEX_SMS, SmsRecordItem.class);
            if(!CollectionUtils.isEmpty(matchRecords)){
                Long createTime = matchRecords.get(0).getCreateTime();//拿到最近拉取短信时间
                //按照address+body+date去重
                matchRecords = matchRecords.stream().filter(Objects::nonNull).filter(i->i.getAddress()!=null).filter(i->i.getDate()!=null).collect(Collectors.toList());
                Comparator<SmsRecordItem> byName = Comparator.comparing(SmsRecordItem::getAddress);
                Comparator<SmsRecordItem> bySizeDesc = Comparator.comparing(SmsRecordItem::getDate);
                matchRecords.sort(byName.thenComparing(bySizeDesc)); // 先以adress升序排列，再按照create_time升叙排列
                List<SmsRecordItem> collect = matchRecords.stream().collect(CollectorExt.distinctBy(t -> t.getAddress() + t.getBody() + t.getDateString()));
                smsElasticPO.setSms_record_item(collect);
                smsElasticPO.setCreate_time(createTime);
            }
            log.info("根据用户id查询到短信记录，userId={}",userId);
        }
        return smsElasticPO;
    }




    @Resource
    private RestClientBuilder restClientBuilder;

    /**
     * 根据条件在指定索引中查询
     * 查询出所有数据
     */
    private <T> List<T> find(SearchSourceBuilder sourceBuilder, String index, Class<T> tClass){
        List<T> result = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(index);
        sourceBuilder.from(0);
        sourceBuilder.size(1000);//每次查询最大数量
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
        SearchResponse searchResponse = null;
        try {
            //client = esClientPool.getClient();
            Scroll scroll = new Scroll(new TimeValue(60, TimeUnit.SECONDS));//仅指查询60秒超时，非总共爬取数据用时
            searchRequest.scroll(scroll);
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.status() != RestStatus.OK){
                log.error("搜索错误，查询响应码={}", searchResponse.status());
            }
            String scrollId = searchResponse.getScrollId();
            SearchHit[] hits = searchResponse.getHits().getHits();
            List<SearchHit> resultSearchHit = new ArrayList<>();
            while(ArrayUtils.isNotEmpty(hits)){
                for (SearchHit hit : hits) {
                    resultSearchHit.add(hit);
                }
                SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
                searchScrollRequest.scroll(scroll);
                SearchResponse searchScrollResponse = client.scroll(searchScrollRequest, RequestOptions.DEFAULT);
                if (searchScrollResponse.status() != RestStatus.OK){
                    log.error("搜索错误，查询响应码={}", searchScrollResponse.status());
                }
                scrollId = searchScrollResponse.getScrollId();
                hits = searchScrollResponse.getHits().getHits();
            }
            result = getResult(resultSearchHit, tClass);
        } catch (Exception e) {
            log.error("elasticsearch查询报错", e);
            throw new RuntimeException("elasticsearch查询报错:"+e.getMessage());
        }finally {
            try{
                //清除游标
                if (searchResponse.getScrollId() != null && !searchResponse.getScrollId().equals("")){
                    ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                    clearScrollRequest.addScrollId(searchResponse.getScrollId());
                    ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);
                    if (!clearScrollResponse.isSucceeded()){
                        log.error("清除游标失败，error={}",clearScrollResponse.toString());
                    }
                }
            }catch (Exception e){
                log.error("清除游标异常", e);
            }

        }
        return result;
    }

    /**
     * 根据条件在指定索引中查询,得到匹配的文档数量
     */
    private long count(SearchSourceBuilder sourceBuilder, String index){
        long cnt = 0;
        CountRequest countRequest = new CountRequest(index);
        countRequest.source(sourceBuilder);
        RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
//        log.info(sourceBuilder.toString());
//        long start = System.currentTimeMillis();
        try {
            CountResponse response = client.count(countRequest, RequestOptions.DEFAULT);
            if (response.status() != RestStatus.OK){
                log.error("搜索错误，查询响应码={}", response.status());
            }
            cnt = response.getCount();
        } catch (Exception e) {
            log.error("elasticsearch查询报错", e);
            throw new RuntimeException("elasticsearch查询报错:"+e.getMessage());
        } finally {

        }
//        log.info("count耗时{}ms", System.currentTimeMillis()-start);
        return cnt;
    }

    /**
     * 解析命中的结果数据
     * @param hits
     * @param tClass
     * @param <T>
     * @return
     */
    private <T> List<T> getResult(List<SearchHit> hits, Class<T> tClass){
        List<T> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            list.add(JSONObject.parseObject(sourceAsString, tClass));
        }
        return list;
    }

}
