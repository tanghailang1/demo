package com.example.demo.entity.es;

import lombok.Data;

/**
 * created by DengJin on 2020/10/26 16:37
 */
@Data
public class SmsRecordItem {
//    private Long recordId;
    //时间
    private Long date;

    //发送人
    private String address;

    //内容
    private String body;

//    private Boolean marked;
//
    private Integer type;

//    private String userId;

//    private String orderId;

    private Long createTime;

    private String dateString;
}
