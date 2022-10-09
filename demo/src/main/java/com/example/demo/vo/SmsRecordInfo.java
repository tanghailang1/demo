package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

/**
 * 短信记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsRecordInfo {


    private Integer sms_cnt = -999999;//短信记录条数


    private Integer sms_contacts_cnt = -999999;//短信记录联系人数


    private Integer sms_relatives_cnt = -999999;//短信记录中亲友联系人数






}
