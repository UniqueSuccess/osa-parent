package com.goldencis.osa.common.utils;

import com.goldencis.osa.common.entity.MailConfig;
import com.goldencis.osa.common.entity.MailInfo;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 邮件管理类
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-23 15:30
 **/
@Service
public class MailManager {

    /**
     * 发送 邮件方法 (Html格式，支持附件)
     *
     * @return void
     */
    public boolean send(MailInfo mailInfo, MailConfig config) {
        try {
            HtmlEmail email = new HtmlEmail();
            // 配置信息
            email.setHostName(config.getServerAddress());
            email.setFrom(config.getFrom());
            email.setAuthenticator(new DefaultAuthenticator(config.getFrom(), config.getPassword()));
            email.setSmtpPort(config.getPort());
            email.setCharset("UTF-8");

            // 邮件信息
            email.setSubject(mailInfo.getSubject());
            email.setHtmlMsg(mailInfo.getContent());

            // 添加附件
            List<EmailAttachment> attachments = mailInfo.getAttachments();
            if (null != attachments && attachments.size() > 0) {
                for (EmailAttachment attachment : attachments) {
                    email.attach(attachment);
                }
            }

            // 收件人
            List<String> toAddress = mailInfo.getToAddress();
            if (null != toAddress && toAddress.size() > 0) {
                for (String to : toAddress) {
                    email.addTo(to);
                }
            }
            // 抄送人
            List<String> ccAddress = mailInfo.getCcAddress();
            if (null != ccAddress && ccAddress.size() > 0) {
                for (String cc : ccAddress) {
                    email.addCc(cc);
                }
            }
            //邮件模板 密送人
            List<String> bccAddress = mailInfo.getBccAddress();
            if (null != bccAddress && bccAddress.size() > 0) {
                for (String address : bccAddress) {
                    email.addBcc(address);
                }
            }
            email.send();
            System.out.println("邮件发送成功！");
            return true;
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return false;
    }

}
