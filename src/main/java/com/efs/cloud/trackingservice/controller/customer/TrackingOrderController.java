package com.efs.cloud.trackingservice.controller.customer;

import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.TrackingOrderInputDTO;
import com.efs.cloud.trackingservice.enums.EventTypeEnum;
import com.efs.cloud.trackingservice.enums.OrderStatusEnum;
import com.efs.cloud.trackingservice.service.tracking.TrackingOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author jabez.huang
 */
@Api(tags = {"Customer 记录用户支付模块"})
@RequestMapping("/customer/tracking/order")
@Slf4j
@RestController
public class TrackingOrderController {

    @Autowired
    private TrackingOrderService trackingOrderService;

    @ApiOperation(value = "记录支付事件", notes = "记录支付事件")
    @PostMapping
    public ResponseEntity<ServiceResult> eventTrackingCart(
            @RequestParam(value="orderStatus") OrderStatusEnum orderStatusEnum,
            @RequestBody TrackingOrderInputDTO trackingOrderInputDTO
    ){
        return new ResponseEntity<>( trackingOrderService.eventTrackingOrder(trackingOrderInputDTO,orderStatusEnum), HttpStatus.OK);
    }

}
