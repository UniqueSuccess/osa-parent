package com.goldencis.osa.core.service.impl;

import com.goldencis.osa.core.entity.UserUsergroup;
import com.goldencis.osa.core.mapper.UserUsergroupMapper;
import com.goldencis.osa.core.service.IUserUsergroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 用户和用户组关联表 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-10-23
 */
@Service
public class UserUsergroupServiceImpl extends ServiceImpl<UserUsergroupMapper, UserUsergroup> implements IUserUsergroupService {

}
