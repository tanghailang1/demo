package com.example.demo.dto;

import lombok.Data;

import java.util.List;

/**
 * created by DengJin on 2021/6/24 10:49
 */
@Data
public class EmailInfo {
    private String user;//发件人
    private String to;//收件人
    private String server;//邮件服务器域名
    private int port;//邮件服务器端口
    private String username;//发送邮箱用户名
    private String password;//发送邮箱密码
    private String subject;//邮件标题
    private String content;//邮件内容
    private String file_path;//附件地址
    private List<String> cc;//抄送
}
