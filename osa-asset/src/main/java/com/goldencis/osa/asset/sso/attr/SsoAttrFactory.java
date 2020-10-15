package com.goldencis.osa.asset.sso.attr;

import com.goldencis.osa.asset.sso.SsoCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-28 11:54
 **/
@Component
@Configuration
public class SsoAttrFactory {

    private final Logger logger = LoggerFactory.getLogger(SsoAttrFactory.class);

    /**
     * 解析一条规则,规则中多个命令用;隔开
     *
     * @param rule
     * @return
     */
    public List<ISsoAttr> create(String rule) {
        Objects.requireNonNull(rule, "规则不能为null");
        String[] split = rule.split(";");
        List<ISsoAttr> list = new ArrayList<>();
        for (String s : split) {
            ISsoAttr attr = createSingle(s);
            if (Objects.isNull(attr)) {
                logger.warn("解析规则失败 {}", s);
                continue;
            }
            list.add(attr);
        }
        return list;
    }

    /**
     * 解析一条命令
     *
     * @param rule
     * @return
     */
    private ISsoAttr createSingle(String rule) {
        String cmd;
        String param;
        if (rule.contains("=")) {
            cmd = rule.substring(0, rule.indexOf("="));
            param = rule.substring(rule.indexOf("=") + 1);
        } else {
            cmd = rule;
            param = null;
        }
        SsoCmd ssoCmd = SsoCmd.valueOf(cmd);
        if (Objects.isNull(ssoCmd)) {
            return null;
        }
        try {
            return parse(ssoCmd, param);
        } catch (Exception e) {
            logger.error("解析规则失败", e);
        }
        return null;
    }

    private ISsoAttr parse(SsoCmd ssoCmd, String param) throws Exception {
        ISsoAttr attr = ssoCmd.getClazz().newInstance();
        attr.from(param);
        return attr;
    }


}
