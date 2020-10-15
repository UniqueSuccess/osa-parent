package com.goldencis.osa.common.entity;

import lombok.Data;
import org.apache.commons.mail.EmailAttachment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 邮件信息
 * @program: osa-parent
 * @author: wang mc
 * @create: 2019-01-23 15:15
 **/
@Data
public class MailInfo {

    // 收件人
    private List<String> toAddress = null;
    // 抄送人地址
    private List<String> ccAddress = null;
    // 密送人
    private List<String> bccAddress = null;
    // 附件信息
    private List<EmailAttachment> attachments = null;
    // 邮件主题
    private String subject;
    // 邮件的文本内容
    private String content;

    public void addTo(String... to) {
        if (Objects.isNull(toAddress)) {
            toAddress = new ArrayList<>(to.length);
        }
        toAddress.addAll(Arrays.asList(to));
    }

    public void addCc(String... cc) {
        if (Objects.isNull(ccAddress)) {
            ccAddress = new ArrayList<>(cc.length);
        }
        ccAddress.addAll(Arrays.asList(cc));
    }

    public void addBcc(String... bcc) {
        if (Objects.isNull(bccAddress)) {
            bccAddress = new ArrayList<>(bcc.length);
        }
        bccAddress.addAll(Arrays.asList(bcc));
    }

    public void addAttachment(EmailAttachment... attachment) {
        if (Objects.isNull(attachments)) {
            attachments = new ArrayList<>(attachment.length);
        }
        attachments.addAll(Arrays.asList(attachment));
    }
}
