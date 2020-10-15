package com.goldencis.osa.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.core.entity.Dictionary;
import com.goldencis.osa.core.mapper.DictionaryMapper;
import com.goldencis.osa.core.service.IDictionaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-24
 */
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements IDictionaryService {

    private static final String CACHE_NAME = "dictionary";

    @Override
    @Cacheable(value = CACHE_NAME, key = "#root.methodName")
    public List<Dictionary> getAllAssetEncode() {
        return getDictionaryListByType(ConstantsDto.TYPE_ASSET_ENCODE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dictionary> getDictionaryListByType(@NotNull String type) {
        return baseMapper.selectList(new QueryWrapper<Dictionary>().eq("type", type));
    }

    @Cacheable(value = CACHE_NAME, key = "#root.methodName")
    @Override
    public Integer getNormalRoleType() {
        Dictionary dictionary = this.getDictionaryListByTypeAndName(ConstantsDto.TYPE_ROLE_TYPE, ConstantsDto.TYPE_NORMAL);
        if (dictionary != null) {
            return dictionary.getValue();
        }
        return null;
    }

    @Override
    public Integer getRoleType(String roleType) {
        Dictionary dictionary = this.getDictionaryListByTypeAndName(ConstantsDto.TYPE_ROLE_TYPE, roleType);
        if (dictionary != null) {
            return dictionary.getValue();
        }
        return null;
    }

    @Override
    public Dictionary getDictionaryListByTypeAndName(String type, String name) {
        return baseMapper.selectOne(new QueryWrapper<Dictionary>().eq("type", type).eq("name", name));
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#root.methodName")
    public List<Dictionary> getAllSSORuleType() {
        return baseMapper.selectList(new QueryWrapper<Dictionary>().eq("type", ConstantsDto.TYPE_SSO_RULE_TYPE));
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#root.methodName")
    public List<Dictionary> getAllSSORuleAttr() {
        return baseMapper.selectList(new QueryWrapper<Dictionary>().eq("type", ConstantsDto.TYPE_SSO_RULE_ATTR));
    }

    /**
     * 获取默认的跳板机账号
     *
     * @return
     */
    @Override
    public String getDefaultJsAccount() {
        List<Dictionary> list = this.getDictionaryListByType("JS_ACCOUNT");
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0).getNickname();
    }

    /**
     * 获取默认的跳板机密码
     *
     * @return
     */
    @Override
    public String getDefaultJsPassword() {
        List<Dictionary> list = this.getDictionaryListByType("JS_PASSWORD");
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0).getNickname();
    }
}
