package com.goldencis.osa.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.core.entity.Ukey;
import com.goldencis.osa.core.mapper.UkeyMapper;
import com.goldencis.osa.core.params.UkeyParams;
import com.goldencis.osa.core.service.IUkeyService;
import com.goldencis.osa.core.utils.QueryUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * USBKey 服务实现类
 * </p>
 *
 * @author wangmc
 * @since 2019-01-28
 */
@Service
public class UkeyServiceImpl extends ServiceImpl<UkeyMapper, Ukey> implements IUkeyService {

    @Override
    public IPage<Ukey> getUkeyListInPage(UkeyParams params) {
        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Integer count = baseMapper.getUkeyCount(params);
        List<Ukey> list = baseMapper.getUkeyListInPage(params);
        IPage<Ukey> page = new Page<>();
        page.setTotal(Objects.isNull(count) ? 0 : count);
        page.setRecords(list);
        return page;
    }

    /**
     * 保存新的ukey
     */
    @Override
    public void saveUkey(Ukey ukey) {
        Objects.requireNonNull(ukey, "参数不能为空");
        Objects.requireNonNull(ukey.getSign(), "USBKey标识不能为空");
        Objects.requireNonNull(ukey.getName(), "USBKey名称不能为空");
        ukey.setId(UUID.randomUUID().toString());
        ukey.setCreateTime(LocalDateTime.now());
        this.save(ukey);
    }

    @Override
    public List<Map<String, Object>> getUkeyUnused() {
        List<Ukey> list = baseMapper.selectList(new QueryWrapper<Ukey>().isNull("user_guid"));
        return list.stream().map(item -> {
            Map<String, Object> map = new HashMap<>(3);
            map.put("id", item.getId());
            map.put("name", item.getName());
            map.put("sign", item.getSign());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteUkeyById(String id) {
        Objects.requireNonNull(id, "id不能为空");
        Ukey ukey = this.getById(id);
        if (Objects.isNull(ukey)) {
            return;
        }
        if (!StringUtils.isEmpty(ukey.getUserGuid())) {
            throw new IllegalArgumentException("已经绑定用户的USBKey不能删除");
        }
        this.removeById(id);
    }
}
