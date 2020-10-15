package com.goldencis.osa.core.service;

import com.goldencis.osa.core.entity.Dictionary;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 *  字典表服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-24
 */
public interface IDictionaryService extends IService<Dictionary> {

    /**
     * 获取所有的设备编码
     * @return
     */
    List<Dictionary> getAllAssetEncode();

    /**
     * 根据类型获取字典表集合
     * @param type 字典表中type字段
     * @return
     */
    List<Dictionary> getDictionaryListByType(@NotNull String type);

    /**
     * 获取普通类型的用户角色类型对应的值
     * @return 用户角色类型对应的值
     */
    Integer getNormalRoleType();

    /**
     * 根据类型获取角色类型对应的值
     * @return 用户角色类型对应的值
     */
    Integer getRoleType(String roleType);

    /**
     * 根据类型和名称获取字典表对象
     * @param type 字典表中type字段
     * @param name 字典表中name字段
     * @return 字典表对象
     */
    Dictionary getDictionaryListByTypeAndName(String type, String name);

    /**
     * 获取SSO所有的规则类型
     * @return
     */
    List<Dictionary> getAllSSORuleType();

    /**
     * 获取SSO所有的规则属性
     * @return
     */
    @Deprecated
    List<Dictionary> getAllSSORuleAttr();

    /**
     * 获取默认的跳板机账号
     * @return
     */
    String getDefaultJsAccount();

    /**
     * 获取默认的跳板机密码
     * @return
     */
    String getDefaultJsPassword();
}
