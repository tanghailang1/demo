package com.efs.cloud.trackingservice.controller.internel;

import com.efs.cloud.trackingservice.ServiceResult;
import com.efs.cloud.trackingservice.dto.wechat.WXDateInputDTO;
import com.efs.cloud.trackingservice.service.wechat.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author jabez.huang
 */
@RestController
@Slf4j
public class WechatController {

    @Autowired
    private WXRetainService wxRetainService;
    @Autowired
    private WXTrendService wxTrendService;
    @Autowired
    private WXUserService wxUserService;
    @Autowired
    private WXVisitService wxVisitService;
    @Autowired
    private WXPerformanceService wxPerformanceService;

    @ApiOperation(value = "获取用户访问小程序日留存", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/retain/daily")
    public ResponseEntity<ServiceResult> wechatRetainDaily(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxRetainService.getDailyRetain(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户访问小程序月留存(日期必须是自然月)", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/retain/monthly")
    public ResponseEntity<ServiceResult> wechatRetainMonthly(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxRetainService.getMonthlyRetain(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户访问小程序周留存(日期必须是自然周)", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/retain/weekly")
    public ResponseEntity<ServiceResult> wechatRetainWeekly(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxRetainService.getWeeklyRetain(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户访问小程序数据日趋势", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/trend/daily")
    public ResponseEntity<ServiceResult> wechatTrendDaily(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxTrendService.getDailyVisitTrend(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户访问小程序数据月趋势(能查询到的最新数据为上一个自然月的数据)", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/trend/monthly")
    public ResponseEntity<ServiceResult> wechatTrendMonthly(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxTrendService.getMonthVisitTrend(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户访问小程序数据周趋势", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/trend/weekly")
    public ResponseEntity<ServiceResult> wechatTrendWeekly(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxTrendService.getWeeklyVisitTrend(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户访问小程序数据概况", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/trend/summary")
    public ResponseEntity<ServiceResult> wechatTrendSummary(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxTrendService.getDailySummary(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户访问小程序数据概况", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/visit/page")
    public ResponseEntity<ServiceResult> wechatVisitPage(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxVisitService.getVisitPage(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户小程序访问分布数据(来源)", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/visit/source")
    public ResponseEntity<ServiceResult> wechatVisitSource(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxVisitService.getVisitSource(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户小程序访问分布数据(时长)", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/visit/staytime")
    public ResponseEntity<ServiceResult> wechatVisitStayTime(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxVisitService.getVisitStayTime(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户小程序访问分布数据(深度)", httpMethod = "POST", response = ServiceResult.class)
    @PostMapping("/visit/depth")
    public ResponseEntity<ServiceResult> wechatVisitDepth(
            @RequestBody WXDateInputDTO wxDateInputDTO) {
        ServiceResult serviceResult = null;
        try {
            serviceResult = wxVisitService.getVisitDepth(wxDateInputDTO);
        } catch (Exception e) {
            log.info("====>" + e);
            return new ResponseEntity<>(ServiceResult.builder().code(-1009).msg("error").data(e).build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

}
