package com.efs.cloud.trackingservice.controller.customer;

import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.TrackingPageInputDTO;
import com.efs.cloud.trackingservice.service.tracking.TrackingPageService;
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

@Api(tags = {"Customer 记录用户页面模块"})
@RequestMapping("/customer/tracking/page")
@Slf4j
@RestController
public class TrackingPageController {

    @Autowired
    private TrackingPageService trackingPageService;


    @ApiOperation(value = "记录跟踪页面", notes = "记录跟踪页面")
    @PostMapping
    public ResponseEntity<ServiceResult> pageTrackingView(
            @RequestHeader(value = "jwt", required = false) String jwt,
            @RequestBody TrackingPageInputDTO trackingPageInputDTO
    ){
        return new ResponseEntity<>( trackingPageService.pageTrackingView(jwt,trackingPageInputDTO), HttpStatus.OK);
    }

    @ApiOperation(value = "获取全局唯一UnionId", notes = "获取全局唯一UnionId")
    @GetMapping("/union_id")
    public ResponseEntity<ServiceResult> pageGetUnionId(){
        return new ResponseEntity<>( trackingPageService.pageGetUnionId(), HttpStatus.OK);
    }

}
