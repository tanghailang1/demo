package com.efs.cloud.trackingservice.controller.customer;

import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.TrackingActionInputDTO;
import com.efs.cloud.trackingservice.enums.EventTypeEnum;
import com.efs.cloud.trackingservice.service.tracking.TrackingActionService;
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

@Api(tags = {"Customer 记录用户行为模块"})
@RequestMapping("/customer/tracking/action")
@Slf4j
@RestController
public class TrackingActionController {

    @Autowired
    private TrackingActionService trackingActionService;

    @ApiOperation(value = "记录页面行为事件", notes = "记录页面行为事件")
    @PostMapping
    public ResponseEntity<ServiceResult> eventTrackingAction(
            @RequestParam(value="eventType") EventTypeEnum eventTypeEnum,
            @RequestBody TrackingActionInputDTO trackingActionInputDTO
    ){
        return new ResponseEntity<>( trackingActionService.eventTrackingAction(trackingActionInputDTO, eventTypeEnum), HttpStatus.OK);
    }

}
