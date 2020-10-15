package com.goldencis.osa.session.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.goldencis.osa.session.entity.TerminalCommand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-11-09
 */
public interface ITerminalCommandService extends IService<TerminalCommand> {

    /**
     * 获取命令的分页列表
     * @param params 参数集合
     */
    IPage<TerminalCommand> getCommandListInPage(Map<String, String> params);
}
