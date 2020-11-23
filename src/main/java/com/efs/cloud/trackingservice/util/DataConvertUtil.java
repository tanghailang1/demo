package com.efs.cloud.trackingservice.util;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * 数据转换工具类
 * @author jabez.huang
 */
public class DataConvertUtil {

    public static String objectConvertJson(Object data){
        HashMap<String,String> dataMap = (HashMap<String, String>) data;
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll( dataMap );
        return jsonObject.toJSONString();
    }

}
