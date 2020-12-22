package com.efs.cloud.trackingservice.controller.internel;

import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.service.OrderComparisonService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jabez.huang
 */
@RequestMapping("/internel")
@Slf4j
@RestController
public class TrackingController {

    @Autowired
    private OrderComparisonService orderComparisonService;

    @ApiOperation(value = "比对tracking订单", httpMethod = "GET", response = ServiceResult.class)
    @GetMapping("/tracking/order/comparison")
    public ResponseEntity<ServiceResult> CloudOrderComparison(
            @RequestParam(value = "date_time") String dateTime,
            @RequestParam(value = "merchant_id") Integer merchantId) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = orderComparisonService.OrderComparison(merchantId,dateTime);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }
}
