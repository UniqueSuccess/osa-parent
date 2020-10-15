package com.goldencis.osa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.service.IUserService;
import com.goldencis.osa.system.entity.AuditorOperator;
import com.goldencis.osa.system.mapper.AuditorOperatorMapper;
import com.goldencis.osa.system.service.IAuditorOperatorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <p>
 * 审计员、操作员 关联表 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-04
 */
@Service
public class AuditorOperatorServiceImpl extends ServiceImpl<AuditorOperatorMapper, AuditorOperator> implements IAuditorOperatorService {

    @Autowired
    IUserService userService;

    @Autowired
    AuditorOperatorMapper auditorOperatorMapper;

    /**
     * 新增 、编辑 审计员设置操作员权限
     * @param userGuid 审计员 id
     * @param operatorIds 操作员 ids ,分割
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void saveOrUpdateAuditorOperators(String userGuid, String operatorIds) {
        if (StringUtils.isEmpty(userGuid)){
            throw new IllegalArgumentException("审计员为空");
        }
        if (StringUtils.isEmpty(operatorIds)){
            throw new IllegalArgumentException("操作员为空");
        }
        if (Objects.isNull(userService.findUserByGuid(userGuid))) {
            throw new IllegalArgumentException("审计员不存在");
        }

        Arrays.asList(operatorIds.split(",")).stream() .forEach(operatorId -> {
            if (Objects.isNull(userService.findUserByGuid(userGuid))) {
                throw new IllegalArgumentException("审计员不存在");
            }
        });

       List<String> operatorIdLists =  Arrays.asList(operatorIds.split(",")) ;
       if (ListUtils.isEmpty(operatorIdLists)){
           throw new IllegalArgumentException("操作员为空");
       }
        //先删除原有数据
        auditorOperatorMapper.delete(new QueryWrapper<AuditorOperator>().eq("auditor_guid",userGuid));

        operatorIdLists.stream().forEach(operatorId -> auditorOperatorMapper.insert(new AuditorOperator(userGuid,operatorId)));
    }

    /**
     * 根据审计员guid 获取操作员列表
     * @param userGuid 审计员guid
     * @return 操作员列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getOperatorListByAuditorGuid(String userGuid) {
        //罗列选中内容
        List<AuditorOperator> auditorOperators = new ArrayList<>();

        //用户guid不为空，判断 用户存不存在
        if (!org.springframework.util.StringUtils.isEmpty(userGuid)){
            if (Objects.isNull(userService.findUserByGuid(userGuid))) {
                throw new IllegalArgumentException("审计员不存在");
            }
            //罗列选中内容
            auditorOperators = auditorOperatorMapper.selectList(new QueryWrapper<AuditorOperator>().eq("auditor_guid",userGuid));
        }

        //罗列设备组
        List<User> opeartorList  = userService.getAllOperatorList();
        if (!ListUtils.isEmpty(opeartorList)) {
            List<AuditorOperator> finalUserOperators = auditorOperators;
            opeartorList.stream().forEach(new Consumer<User>() {
                @Override
                public void accept(User user) {
                    if(!ListUtils.isEmpty(finalUserOperators)){
                        if ( finalUserOperators.stream().anyMatch(auditorOperator -> user.getGuid().equals(auditorOperator.getOperatorGuid()))){
                            user.setChecked(true);
                        }
                    }
                }
            });
        }
        return opeartorList;
    }
}
