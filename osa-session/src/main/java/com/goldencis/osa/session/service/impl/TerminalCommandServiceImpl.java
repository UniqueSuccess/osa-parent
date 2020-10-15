package com.goldencis.osa.session.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.DateUtil;
import com.goldencis.osa.core.utils.QueryUtils;
import com.goldencis.osa.session.entity.TerminalCommand;
import com.goldencis.osa.session.mapper.TerminalCommandMapper;
import com.goldencis.osa.session.service.ITerminalCommandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-09
 */
@Service
public class TerminalCommandServiceImpl extends ServiceImpl<TerminalCommandMapper, TerminalCommand> implements ITerminalCommandService {

    @Autowired
    private TerminalCommandMapper commandMapper;

    @Override
    public IPage<TerminalCommand> getCommandListInPage(Map<String, String> params) {
        Page page = new Page();

        //为模糊查询的添加增加%%符号
        QueryUtils.addFuzzyQuerySymbols(params);
        Map<String, Object> paramMap = QueryUtils.formatPageParams(params);

        //增加默认排序
        if (!params.containsKey("orderType")) {
            params.put("orderType", ConstantsDto.ORDER_TYPE_DESC);
        }
        if (!params.containsKey("orderColumn")) {
            params.put("orderColumn", "timestamp");
        }

        int total = commandMapper.countCommandsInPage(params);
        List<TerminalCommand> commandList = commandMapper.getCommandsInPage(params);
        this.formatCommandList(commandList);

        page.setTotal(total);
        page.setRecords(commandList);
        return page;
    }

    private void formatCommandList(List<TerminalCommand> commandList) {
        for (TerminalCommand command : commandList) {
            Integer timestamp = command.getTimestamp();
            LocalDateTime time =LocalDateTime.ofEpochSecond(timestamp,0, ZoneOffset.ofHours(8));
            command.setTime(time);
        }
    }
}
