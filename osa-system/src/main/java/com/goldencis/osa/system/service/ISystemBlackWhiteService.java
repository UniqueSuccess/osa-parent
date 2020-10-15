package com.goldencis.osa.system.service;

import com.goldencis.osa.system.entity.SystemBlackWhite;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统配置--> 管控平台--> 黑白名单 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-27
 */
public interface ISystemBlackWhiteService extends IService<SystemBlackWhite> {

    /**
     * 保存黑白名单
     * @param json 黑白名单JSON
     */
    void saveBlackWhite(String json);

    /**
     * 获取 黑白名单
     * @return 黑白名单列表
     */
    String getBlackWhite();
}
