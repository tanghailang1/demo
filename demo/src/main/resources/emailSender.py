# -*- coding: utf-8 -*-
import os
import smtplib
import sys
from email import encoders
from email.mime.base import MIMEBase
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

class SendMail(object):
    def __init__(self, email_info):
        self.email_info = email_info
        # 使用SMTP_SSL连接端口为465
        self.smtp = smtplib.SMTP_SSL(self.email_info['server'], self.email_info['port'])
        # 创建两个变量
        self._attachements = []
        self._from = ''
    def login(self):
        # 通过邮箱名和smtp授权码登录到邮箱
        self._from = self.email_info['user']
        self.smtp.login(self.email_info['user'], self.email_info['password'])
        print('login success')
    # def _format_addr(self, s):
    #     name, addr = parseaddr(s)
    #     return formataddr((Header(name, 'utf-8').encode(), addr))

    def add_attachment(self):
        # 添加附件内容
        # 注意：添加附件内容是通过读取文件的方式加入
        file_path = self.email_info['file_path']
        with open(file_path, 'rb') as file:
            filename = os.path.split(file_path)[1]
            mime = MIMEBase('application', 'octet-stream', filename=filename)
            mime.add_header('Content-Disposition', 'attachment', filename=('gbk', '', filename))
            mime.add_header('Content-ID', '<0>')
            mime.add_header('X-Attachment-Id', '0')
            mime.set_payload(file.read())
            encoders.encode_base64(mime)
            # 添加到列表，可以有多个附件内容
            self._attachements.append(mime)

    def sendMail(self):
        # 发送邮件，可以实现群发
        msg = MIMEMultipart()
        contents = MIMEText(self.email_info['content'], 'plain', 'utf-8')
        msg['From'] = self.email_info['user']
        msg['To'] = self.email_info['to']
        msg['Subject'] = self.email_info['subject']
        cc = ','.join(self.email_info['cc'])
        msg['Cc'] = cc

        for att in self._attachements:
            # 从列表中提交附件，附件可以有多个
            msg.attach(att)
        msg.attach(contents)
        try:
            self.smtp.sendmail(self._from, self.email_info['to'].split(',')+cc.split(','), msg.as_string())
            print('Email Send Success!'.center(30, '#'))
        except Exception as e:
            print('Error:', e)

    def close(self):
        # 退出smtp服务
        self.smtp.quit()
        print('logout'.center(30, '#'))


# email_dict = {
#     # 手动填写，确保信息无误
#     "user": "dengj@hibay.cc",
#     "to": "yezj0010@163.com", # 多个邮箱以','隔开；
#     "server": "smtp.exmail.qq.com",
#     'port': 465,    # values值必须int类型
#     "username": "dengj@hibay.cc",
#     "password": "Yezj0010",
#     "subject": "还款明细",
#     "content": '还款明细，见附件',
#     'file_path': 'D:/data/1/2/repayInfo_20210624100221.xlsx',
#     'cc': ['dengjin0010@gmail.com', 'dengjin0010@gmail.com']
# }

email_dict = eval(sys.argv[1])

sendmail = SendMail(email_dict)
sendmail.login()
sendmail.add_attachment()
sendmail.sendMail()
sendmail.close()