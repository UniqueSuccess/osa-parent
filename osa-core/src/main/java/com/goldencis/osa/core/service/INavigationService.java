package com.goldencis.osa.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.goldencis.osa.core.entity.Navigation;

import java.util.List;

/**
 * <p>
 * 页签-导航信息表 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
public interface INavigationService extends IService<Navigation> {

    /**
     * 获取全部菜单集合
     * @return
     */
    List<Navigation> getAllNavigations();

    /**
     * 将菜单集合转化为菜单树
     * @param navigationList 菜单集合
     * @return 菜单树
     */
    List<Navigation> formatNavigationTree(List<Navigation> navigationList);
}
