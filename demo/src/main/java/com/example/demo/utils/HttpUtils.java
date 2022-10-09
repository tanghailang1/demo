package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

import java.util.concurrent.TimeUnit;


@Slf4j
public class HttpUtils {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public static boolean postSend(String url, String data) {
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            log.info("url = {},result = {}", url, result);
            return true;
        } catch (Exception e) {
            log.error("okhttp3发送异常,url={}", url, e);
            return false;
        }
    }

    public static boolean postSend(String url, String data, String info) {
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            log.info("url = {},traceInfo={},result = {}", url, info, result);
            return true;
        } catch (Exception e) {
            log.error("okhttp3发送异常,url={},traceInfo={}", url, info, e);
            return false;
        }
    }

    public static String postSend(Request request, String info) {
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            log.info("url = {},traceInfo={},result = {}", request.url().toString(), info, result);
            return result;
        } catch (Exception e) {
            log.error("okhttp3发送异常,url={},traceInfo={}", request.url().toString(), info, e);
            return null;
        }
    }

    /**
     * 解析请求地址
     * @param address
     * @return HttpHost
     */
    public static HttpHost[] makeHttpHost(String address) {
        if(StringUtils.isEmpty(address)){
            throw new RuntimeException("address为空");
        }
        String[] addressArr = address.split("[,;]");

        HttpHost[] httpHosts = new HttpHost[addressArr.length];
        int index = 0;
        for(String info : addressArr) {
            String[] single = info.split(":");
            String ip = single[0];
            int port = Integer.parseInt(single[1]);
            httpHosts[index] = new HttpHost(ip, port, "http");
            index++;
        }
        return httpHosts;
    }
}
