package com.efs.cloud.trackingservice.controller.customer;

import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.TrackingLogInputDTO;
import com.efs.cloud.trackingservice.service.tracking.TrackingLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author maxun
 */
@Api(tags = {"Customer 记录日志模块"})
@RequestMapping("/customer/tracking/log")
@Slf4j
@RestController
public class TrackingLogController {

    @Autowired
    private TrackingLogService trackingLogService;

    @ApiOperation(value = "记录日志事件", notes = "记录日志事件" ,response = TrackingLogInputDTO.class)
    @PostMapping
    public ResponseEntity<ServiceResult> eventTrackingLog(
            @RequestBody TrackingLogInputDTO trackingLogInputDTO
    ){
        return new ResponseEntity<>( trackingLogService.eventTrackingLog(trackingLogInputDTO), HttpStatus.OK);
    }

}
