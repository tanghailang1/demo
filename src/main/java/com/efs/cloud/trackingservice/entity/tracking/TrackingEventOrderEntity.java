package com.efs.cloud.trackingservice.entity.tracking;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.efs.cloud.trackingservice.entity.entity.OrderItemDTOEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author jabez.huang
 */
@Entity
@Table(name = "tracking_event_order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrackingEventOrderEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer toId;
    private Integer orderAmount;
    private Integer orderShippingFee;
    private Integer orderDiscountAmount;
    @Column(name = "order_items", columnDefinition = "json", nullable = true)
    private String orderItems;
    private Integer orderSubtotal;
    private String ip;
    private Integer scene;
    private String campaign;
    private String orderId;
    private String uniqueId;
    private String status;
    private Integer customerId;
    private Integer merchantId;
    private Integer storeId;
    private String data;
    @Temporal(TemporalType.DATE)
    @JSONField(format = "yyyy-MM-dd")
    private Date createDate;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public List<OrderItemDTOEntity> getOrderItems(){
        return JSON.parseArray(this.orderItems, OrderItemDTOEntity.class);
    }

    public JSONObject toJSONObject(){
        return (JSONObject) JSONObject.toJSON(this);
    }

}
