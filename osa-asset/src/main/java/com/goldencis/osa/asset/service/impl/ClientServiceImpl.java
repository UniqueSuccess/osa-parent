package com.goldencis.osa.asset.service.impl;

import com.goldencis.osa.asset.entity.Client;
import com.goldencis.osa.asset.mapper.ClientMapper;
import com.goldencis.osa.asset.service.IClientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-03
 */
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements IClientService {

}
