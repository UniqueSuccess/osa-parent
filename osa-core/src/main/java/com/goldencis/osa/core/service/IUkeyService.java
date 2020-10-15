package com.goldencis.osa.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.core.entity.Ukey;
import com.goldencis.osa.core.params.UkeyParams;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * USBKey 服务类
 * </p>
 *
 * @author wangmc
 * @since 2019-01-28
 */
public interface IUkeyService extends IService<Ukey> {

    /**
     * 分页获取ukey列表
     * @param params
     * @return
     */
    IPage<Ukey> getUkeyListInPage(UkeyParams params);

    /**
     * 保存新的ukey
     */
    void saveUkey(Ukey ukey);

    /**
     * 获取目前没有绑定的ukey信息
     * @return
     */
    List<Map<String, Object>> getUkeyUnused();

    /**
     * 根据id删除USBKey
     * @param id
     */
    void deleteUkeyById(String id);
}
