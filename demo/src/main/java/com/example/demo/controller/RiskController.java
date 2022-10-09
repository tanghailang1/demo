package com.example.demo.controller;

import com.example.demo.dao.risk.TaskDao;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/risk")
public class RiskController {

    @Resource
    private TaskDao taskDao;

}
