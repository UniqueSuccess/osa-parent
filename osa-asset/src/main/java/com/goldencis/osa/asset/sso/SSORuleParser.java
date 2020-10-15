package com.goldencis.osa.asset.sso;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.asset.entity.SsoRuleAttr;
import com.goldencis.osa.asset.service.ISsoRuleAttrService;
import com.goldencis.osa.asset.sso.attr.ISsoAttr;
import com.goldencis.osa.asset.sso.attr.SsoAttrFactory;
import com.goldencis.osa.asset.sso.replace.IRulePlaceholder;
import com.goldencis.osa.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 解析SSO(单点登录)规则
 *
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-07 11:32
 **/
@Component
public class SSORuleParser {

    private static final String SEPARATOR = ";";
    private final Logger logger = LoggerFactory.getLogger(SSORuleParser.class);
    @Autowired
    private ISsoRuleAttrService ssoRuleAttrService;
    @Autowired
    private SsoAttrFactory ssoAttrFactory;


    public List<SsoRuleAttr> parse(@NotNull String rule) throws IllegalArgumentException {
        return parse(Arrays.asList(rule.split(SEPARATOR)));
    }

    public List<SsoRuleAttr> parse(@NotNull List<String> list) throws IllegalArgumentException {
        List<SsoRuleAttr> attrs = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            int index = s.indexOf("=");
            String nickName = s.substring(0, index);
            SsoRuleAttr attr = getSSORuleAttrByNickname(nickName);
            if (attr == null) {
                throw new IllegalArgumentException(String.format("不存在的规则: %s", nickName));
            }
            String content = s.substring(index + 1);
            attr.setContent(StringUtils.isEmpty(content) ? attr.getDefaultContent() : content);
            attr.setCompositor(i);
            attrs.add(attr);
        }
        return attrs;
    }

    @Cacheable(value = "SSORuleAttr", key = "#nickname")
    public SsoRuleAttr getSSORuleAttrByNickname(String nickname) {
        return ssoRuleAttrService.getOne(new QueryWrapper<SsoRuleAttr>().eq("nickname", nickname));
    }

    /**
     * 替换sso规则中的占位符
     * @return
     */
    public String placeHolder(String rule, IRulePlaceholder... holderList) {
        return placeHolder(rule, Arrays.asList(holderList));
    }

    /**
     * 替换sso规则中的占位符
     * @return
     */
    public String placeHolder(String rule, List<IRulePlaceholder> holderList) {
        Objects.requireNonNull(rule, "规则不能为空");
        if (Objects.isNull(holderList) || holderList.isEmpty()) {
            return rule;
        }
        for (IRulePlaceholder holder : holderList) {
            if (StringUtils.isEmpty(holder.placeholder())) {
                throw new IllegalArgumentException("占位符不能为null");
            }
            if (StringUtils.isEmpty(holder.content())) {
                logger.warn("内容为空,不进行替换,placeholder {}", holder.placeholder());
                continue;
            }
            rule = rule.replace(holder.placeholder(), holder.content());
        }
        return rule;
    }

    public JSONArray ruleToJson(String rule) {
        List<ISsoAttr> list = ssoAttrFactory.create(rule);
        return JSONArray.parseArray(JSON.toJSONString(list));
    }
}
