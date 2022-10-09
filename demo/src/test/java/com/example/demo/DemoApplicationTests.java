package com.example.demo;

import com.example.demo.entity.es.SmsElasticPO;
import com.example.demo.entity.es.SmsRecordItem;
import com.example.demo.entity.risk.TestTask2Po;
import com.example.demo.entity.risk.TestTaskPo;
import com.example.demo.service.impl.ElasticSearchService;
import com.example.demo.utils.GeneralConvertorUtils;
import com.example.demo.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private RedisUtil redisUtil;

    @Resource
    GeneralConvertorUtils generalConvertor;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Test
    void contextLoads() {
    }


    @Test
    public void test1(){
        boolean tang = redisUtil.tryLock("tang", 120);

    }


    @Test
    public void test2(){
        List<TestTaskPo> testTaskPos = Arrays.asList(TestTaskPo.builder().id(1).name("111").build(), TestTaskPo.builder().id(1).name("111").build(), TestTaskPo.builder().id(1).name("111").build(), TestTaskPo.builder().id(1).name("111").build(),
                TestTaskPo.builder().id(1).name("111").build(), TestTaskPo.builder().id(1).name("111").build());
        List<TestTask2Po> convertor =  generalConvertor.convertor(testTaskPos, TestTask2Po.class);
        System.out.println(convertor);

    }


    @Test
    public void test3(){
        SmsElasticPO smsElasticPO = elasticSearchService.findSmsByUserIdOrderByAddressTime("6211af8a36ce5709940d8505","1234123");
        List<SmsRecordItem> sms_record_item = smsElasticPO.getSms_record_item();
        System.out.println(sms_record_item.size());
    }


}
