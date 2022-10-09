package com.example.demo;

import com.example.demo.config.redis.RedisConfig;
import com.example.demo.utils.RedisUtil;
import com.example.demo.vo.SmsRecordInfo3;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

public class Test1 {


    public static void main(String[] args) {
//        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
////设置密钥
//        encryptor.setPassword("qwer1234tyu");
////设置加密算法
//        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
////加密信息
//        String encryptedText = encryptor.encrypt("KexupDTUDWUe6YzH8uXj2vTk");
//        System.out.println("encryptedText:"+  encryptedText);
////解密
//        String decryptedText = encryptor.decrypt("IWvsrJqJmAgGK8C4bHY2HAZ/0oB411djoaKui7IQ5wdv+cUcqzleuw==");
//        System.out.println("decryptedText:"+  decryptedText);
//
//
//        encrytion();

//        SmsRecordInfo3 smsRecordInfo = new SmsRecordInfo3();
//        smsRecordInfo.setSms_cnt3(1);
//        smsRecordInfo.setSms_relatives_cnt3(2);
//        smsRecordInfo.setSms_contacts_cnt3(3);
//        System.out.println(smsRecordInfo.toString());



    }


    public static void encrytion() {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword("qwer1234tyu");
        //要加密的数据（数据库的用户名或密码）
        String password = textEncryptor.encrypt("KexupDTUDWUe6YzH8uXj2vTk");

        //解密：
        //textEncryptor.decrypt("");

        System.out.println("password:"+password);

    }





}
