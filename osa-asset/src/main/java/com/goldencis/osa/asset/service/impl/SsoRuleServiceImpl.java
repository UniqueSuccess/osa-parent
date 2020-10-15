package com.goldencis.osa.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.asset.entity.SsoRule;
import com.goldencis.osa.asset.mapper.SsoRuleMapper;
import com.goldencis.osa.asset.service.ISsoRuleService;
import com.goldencis.osa.asset.sso.SSORuleParser;
import com.goldencis.osa.core.entity.Dictionary;
import com.goldencis.osa.core.service.IDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * SSO(单点登录)规则 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-07
 */
@Service
public class SsoRuleServiceImpl extends ServiceImpl<SsoRuleMapper, SsoRule> implements ISsoRuleService {

    /**
     * 工具类型前缀
     */
    private static final String PREFIX_TOOL_TYPE = "toolType=";
    /**
     * 规则名称前缀
     */
    private static final String PREFIX_RULE_NAME = "ruleName=";

    @Autowired
    private SSORuleParser ssoRuleParser;
    @Autowired
    private IDictionaryService dictionaryService;

    @Override
    public List<SsoRule> getSSORuleListByAssetId(Integer assetId) {
        if (Objects.isNull(assetId)) {
            return new ArrayList<>(0);
        }
        List<SsoRule> list = baseMapper.getSSORuleListByAssetId(assetId);
        list.forEach(item -> item.setRuleAttrs(ssoRuleParser.parse(item.getRule())));
        return list;
    }

    @Override
    public SsoRule importSSORuleFromFile(File file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        SsoRule rule = new SsoRule();
        List<String> list = reader.lines()
                // 将注释和无用行移除掉
                .filter(s -> {
                    if (s == null) {
                        return false;
                    }
                    s = s.trim();
                    if (s.isEmpty() || s.startsWith("#")) {
                        return false;
                    }
                    if (s.startsWith(PREFIX_TOOL_TYPE)) {
                        rule.setToolTypeName(s.replace(PREFIX_TOOL_TYPE, ""));
                        return false;
                    }
                    if (s.startsWith(PREFIX_RULE_NAME)) {
                        rule.setName(s.replace(PREFIX_RULE_NAME, ""));
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        rule.setToolType(getToolTypeValueByName(rule.getToolTypeName()));
        if (StringUtils.isEmpty(rule.getName())) {
            throw new IllegalArgumentException("规则名称不能为空");
        }
        for (String s : list) {
            if (s.contains(";")) {
                throw new IllegalArgumentException(String.format("规则中含有分号: %s", s));
            }
            if (!s.contains("=")) {
                throw new IllegalArgumentException(String.format("规则格式不正确: %s", s));
            }
        }
        String ruleStr = String.join(";", list);
        rule.setRule(ruleStr);
        rule.setRuleAttrs(ssoRuleParser.parse(list));
        return rule;
    }

    private Integer getToolTypeValueByName(String name) throws IllegalArgumentException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("工具类型不能为空");
        }
        List<Dictionary> list = dictionaryService.getAllSSORuleType();
        for (Dictionary item : list) {
            if (name.equals(item.getName())) {
                return item.getValue();
            }
        }
        throw new IllegalArgumentException("没有对应的工具类型");
    }
}
