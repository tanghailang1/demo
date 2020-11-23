package com.efs.cloud.trackingservice.controller.customer;

import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.TrackingCartInputDTO;
import com.efs.cloud.trackingservice.service.tracking.TrackingCartService;
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

@Api(tags = {"Customer 记录商品加购模块"})
@RequestMapping("/customer/tracking/cart")
@Slf4j
@RestController
public class TrackingCartController {

    @Autowired
    private TrackingCartService trackingCartService;

    @ApiOperation(value = "记录商品加购事件", notes = "记录商品加购事件")
    @PostMapping
    public ResponseEntity<ServiceResult> eventTrackingCart(
            @RequestBody TrackingCartInputDTO trackingCartInputDTO
    ){
        return new ResponseEntity<>( trackingCartService.eventTrackingCart(trackingCartInputDTO), HttpStatus.OK);
    }

}