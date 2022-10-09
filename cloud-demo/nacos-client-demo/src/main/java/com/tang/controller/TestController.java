package com.tang.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: tang
 * @Date: 2022/8/15 16:12
 */
@RestController
public class TestController {

    @Value("${test.name}")
    private  String nickname;

    @Value("test.age")
    private  String age;

    @RequestMapping("/test-local-config")
    public String testLocalConfig() {
        return nickname;
    }

}
