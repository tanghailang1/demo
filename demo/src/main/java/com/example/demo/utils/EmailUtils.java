package com.example.demo.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.dao.sass.StatisticalDataDao;
import com.example.demo.dto.EmailInfo;
import com.example.demo.python.StreamGobbler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.util.Arrays;

@Slf4j
public class EmailUtils {

    @Value("${email.python_path}")
    private String pythonPath;
    @Value("${email.send_email}")
    private static String sendEmail;
    @Value("${email.auth_code}")
    private static String authCode;
    @Value("${email.receive_email}")
    private static String receiveEmail;




    public static void main(String[] args) {
        send("risk_group@hibay.cc", "Shusen654321", "383484101@qq.com", "测试邮件", "/Users/sean/Desktop/tt.xlsx","/Users/sean/Desktop/emailSender.py");
    }

    @SneakyThrows
    public static void send(String send, String auth, String receive, String title, String attachmentPath,String pythonPath) {
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setUser(send);
        emailInfo.setTo(receive);
        emailInfo.setServer("smtp.exmail.qq.com");
        emailInfo.setPort(465);
        emailInfo.setUsername(send);
        emailInfo.setPassword(auth);
        emailInfo.setSubject(title);
        emailInfo.setContent("内容见附件");
        emailInfo.setFile_path(attachmentPath);
        emailInfo.setCc(Arrays.asList("luws@hibay.cc", "yinglf@hibay.cc"));
        String ex = JSONObject.toJSONString(emailInfo, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero);
        ex = ex.replaceAll("\"", "'");

        //mac改为python3执行,其他系统为python
        String[] arg = new String[]{"python3", pythonPath, ex};
        log.info("邮件请求信息={},path={}", arg[2],arg[1]);
        Process pr = Runtime.getRuntime().exec(arg);

        InputStream pis = pr.getInputStream();
        InputStream pes = pr.getErrorStream();

        StreamGobbler pisGobbler = new StreamGobbler(pis, "OUTPUT");
        StreamGobbler pesGobbler = new StreamGobbler(pes, "ERROR");
        pisGobbler.start();
        pesGobbler.start();
        pr.waitFor();

        String content = pisGobbler.getContent();//阻塞
        String eContent = pesGobbler.getContent();

        if (content.isEmpty()) {
            log.error("python发送邮件出错:{}", eContent);
        } else {
            log.info(content);
        }
    }
}
