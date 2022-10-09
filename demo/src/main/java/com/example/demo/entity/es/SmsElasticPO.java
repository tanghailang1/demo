package com.example.demo.entity.es;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * indexName: 类似数据库名称
 * type: 类似表名称
 */
@Data
public class SmsElasticPO {
    private String user_id;
    private String order_id;
    private List<SmsRecordItem> sms_record_item = new ArrayList<>();
    private Long create_time;
}
