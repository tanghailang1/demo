package com.efs.cloud.trackingservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * 加密服务
 *
 * @author liming
 */
@Service
@Slf4j
public class EncryptService {
    /**
     * base64 encode
     *
     * @param str
     * @return
     */
    public String base64Encode(String str) throws Exception {
        String result = Base64.getEncoder().encodeToString(str.getBytes("utf-8"));
        return result;
    }

    /**
     * Base64 decode
     *
     * @param str
     * @return
     */
    public String base64Decode(String str) throws Exception {
        byte[] asBytes = Base64.getDecoder().decode(str);
        String result = new String(asBytes, "utf-8");
        return result;
    }
}

