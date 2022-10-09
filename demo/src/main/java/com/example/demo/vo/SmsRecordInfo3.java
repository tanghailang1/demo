package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短信记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsRecordInfo3 {


    private Integer sms_cnt3 = -999999;//短信记录条数


    private Integer sms_contacts_cnt3 = -999999;//短信记录联系人数


    private Integer sms_relatives_cnt3 = -999999;//短信记录中亲友联系人数

    private Integer is_female = -999999;//是否女性用户

    private Integer user_type = -999999;//用户类型（新白户/老白户/非白户）

    private Integer contacts_count = -999999;//通讯录个数

    private Integer contacts_blacklist_count = -999999;//通讯录内联系人中黑名单个数

    private Integer ondebt_order_count = -999999;//系统内在贷订单笔数

    private Integer rejected_orders_count_30d = -999999;//30天内系统历史订单拒绝次数

    private Integer overdue_orders_count_30d = -999999;//30天内系统当前逾期订单数

    private Integer delayed_orders_count_30d = -999999;//30天内系统历史逾期订单数




}
