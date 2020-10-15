package com.goldencis.osa.system.service;

import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.system.entity.AuditorOperator;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 审计员、操作员 关联表 服务类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-04
 */
public interface IAuditorOperatorService extends IService<AuditorOperator> {

    /**
     * 审计员管理操作员权限
     * @param userGuid 审计员 id
     * @param operatorIds 操作员 ids ,分割
     */
    void saveOrUpdateAuditorOperators(String userGuid, String operatorIds);

    /**
     * 通过审计员获取 操作员列表
     * @param userGuid 审计员guid
     * @return 操作员列表
     */
    List<User> getOperatorListByAuditorGuid(String userGuid);
}
