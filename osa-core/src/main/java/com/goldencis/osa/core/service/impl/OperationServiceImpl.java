package com.goldencis.osa.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.core.entity.Operation;
import com.goldencis.osa.core.mapper.OperationMapper;
import com.goldencis.osa.core.service.IOperationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 功能操作表 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-09-27
 */
@Service
public class OperationServiceImpl extends ServiceImpl<OperationMapper, Operation> implements IOperationService {

}
