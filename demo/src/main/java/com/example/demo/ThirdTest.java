package com.example.demo;


import com.example.demo.annotation.LogDeals;
import com.example.demo.utils.SecurityUtil;
import com.example.demo.vo.Result;
import okhttp3.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@RestController
public class ThirdTest {

    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    @LogDeals
    @PostMapping("/yblack")
    public Result yblack(@org.springframework.web.bind.annotation.RequestBody  JSONObject json) throws IOException {
        Long timestamp = System.currentTimeMillis();
        String url = "http://149.129.184.237:12453/risk/blacklist";
        String orderId = UUID.randomUUID().toString();
        String signStr = orderId+timestamp+"a36b29ff75944d38affea0d45a581268";
        String blackUrl = url +"?code=WWMXDTEST"+"&serialNo="+orderId;
        String sign = SecurityUtil.MD5(signStr);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("sign",sign);
        jsonObject.put("mobile",json.get("mobile"));
        jsonObject.put("pan",json.get("pan"));
        jsonObject.put("aadhaar",json.get("addhaar"));
        okhttp3.RequestBody requestBody = RequestBody.create(JSON_TYPE, jsonObject.toJSONString());
        Request request = new Request.Builder()
                .url(blackUrl)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        String result = response.body().string();
        JSONObject respJson = JSONObject.parseObject(result);
        return Result.builder().data(respJson.toJSONString()).build();
    }

    @PostMapping("/ip")
    public String getIpAddress(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {

                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        // ipAddress = this.getRequest().getRemoteAddr();

        return ipAddress;
    }


    @PostMapping("/ippub")
    public void downLoad(HttpServletRequest request) {
        String ip = null;

        // 处理代理情况
        ip = request.getHeader("x-forwarded-for");
        if (ip == null
                || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null
                || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null
                || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                InetAddress inet = null;// 根据网卡取本机配置的IP
                try {
                    inet = InetAddress.getLocalHost();//idea-PC/192.168.212.144
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();//192.168.212.144
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割,多级代理的时候会得到多个以,分割的ip，
        //这时候第一个是真实的客户端ip
        if (ip != null && ip.length() > 15) { // "***.***.***.***".length()
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        System.out.println(ip);
    }
}
