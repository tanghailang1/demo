package com.efs.cloud.trackingservice.config;

import lombok.Data;

/**
 * @author jabez.huang
 */
@Data
public class WXStayTimeConfig {
    private static final String KEY_1 = "0-2s";
    private static final String KEY_2 = "3-5s";
    private static final String KEY_3 = "6-10s";
    private static final String KEY_4 = "11-20s";
    private static final String KEY_5 = "20-30s";
    private static final String KEY_6 = "30-50s";
    private static final String KEY_7 = "50-100s";
    private static final String KEY_8 = ">100s";
}
