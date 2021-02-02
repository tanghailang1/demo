package com.efs.cloud.trackingservice;

/**
 * Global
 *
 */
public class Global {

    private Global() {
        // 私有构建方法
    }

    /**
     * ES: INDEX,TYPE
     */
    public static final String TRACKING_ACTION_INDEX = "cloud_tracking_action";
    public static final String TRACKING_CART_INDEX = "cloud_tracking_cart";
    public static final String TRACKING_ORDER_INDEX = "cloud_tracking_order";
    public static final String TRACKING_PAGE_INDEX = "cloud_tracking_page";
    public static final String TRACKING_LOG_INDEX = "cloud_tracking_log";
    public static final String SALES_ORDER_INDEX = "cloud_sales_order";

    public static final String TRACKING_ACTION_INDEX_TYPE = "cloud_tracking_action_type";
    public static final String TRACKING_CART_INDEX_TYPE = "cloud_tracking_cart_type";
    public static final String TRACKING_ORDER_INDEX_TYPE = "cloud_tracking_order_type";
    public static final String TRACKING_PAGE_INDEX_TYPE = "cloud_tracking_page_type";
    public static final String TRACKING_LOG_INDEX_TYPE = "cloud_tracking_log_type";

}
