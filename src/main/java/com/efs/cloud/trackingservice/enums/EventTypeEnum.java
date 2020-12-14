package com.efs.cloud.trackingservice.enums;

import lombok.Getter;

/**
 * @author jabez.huang
 */

@Getter
public enum EventTypeEnum {
    CLICK_CMS("CLICK_CMS","CMS点击"),
    SEARCH("SEARCH","搜索"),
    SHARE_CARD("SHARE_CARD","卡片分享"),
    SHARE_PLAYBILL("SHARE_PLAYBILL","海报分享"),
    SHARE_CARD_ENTER("SHARE_CARD_ENTER","从卡片进入"),
    SHARE_PLAYBILL_ENTER("SHARE_PLAYBILL_ENTER","从海报进入"),
    SCAN("SCAN","扫码"),
    ADD_CART("ADD_CART","加购"),
    LOGIN("LOGIN","登录"),
    REGISTER("REGISTER","注册"),
    CATEGORY("CATEGORY","分类展现"),
    SAVE_ADDRESS("SAVE_ADDRESS","保存地址"),
    WAIT_PAY_ORDER("WAIT_PAY_ORDER","下单"),
    CANCEL_PAY_ORDER("CANCEL_PAY_ORDER","取消支付"),
    PAY_SUCCESS("PAY_SUCCESS","支付成功"),
    REFUND("REFUND","申请退换货"),
    REFUND_SUCCESS("REFUND_SUCCESS","退款成功"),
    OUT_OF_STOCK("OUT_OF_STOCK","缺货"),
    ARRIVAL_REMIND("ARRIVAL_REMIND","到货提醒"),
    ADD_FAVORITE("ADD_FAVORITE","加入收藏"),
    PDP_ITEM("PDP_ITEM","PDP商品"),
    OTHER("OTHER","其他");

    private String value;
    private String message;
    EventTypeEnum(String value, String message){
        this.value = value;
        this.message = message;
    }
}