package com.goldencis.osa.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goldencis.osa.common.constants.ConstantsDto;
import com.goldencis.osa.common.utils.ListUtils;
import com.goldencis.osa.common.utils.StringUtils;
import com.goldencis.osa.core.entity.User;
import com.goldencis.osa.core.mapper.UserMapper;
import com.goldencis.osa.core.utils.SecurityUtils;
import com.goldencis.osa.system.entity.SystemBlackWhite;
import com.goldencis.osa.system.entity.SystemBlackWhiteEntity;
import com.goldencis.osa.system.mapper.SystemBlackWhiteMapper;
import com.goldencis.osa.system.service.ISystemBlackWhiteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goldencis.osa.system.utils.SystemCons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统配置--> 管控平台--> 黑白名单 服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2018-12-27
 */
@Service
public class SystemBlackWhiteServiceImpl extends ServiceImpl<SystemBlackWhiteMapper, SystemBlackWhite> implements ISystemBlackWhiteService {
    @Autowired
    SystemBlackWhiteMapper systemBlackWhiteMapper;

    @Autowired
    UserMapper userMapper;
    /**
     * 保存黑白名单数据
     * @param json 黑白名单JSON  1黑名单，2 白名单
     * [{"type":1,userIps:["10.10.16.231","10.10.16.231-10.10.16.236"],userNames:["12","33"],assetIps:["10.10.16.21","10.10.16.23"]},
     *  {"type":2,userIps:["10.10.16.235"],userNames:["13","43"],assetIps:["10.10.16.22","10.10.16.26"]}]
     * @return
     */
    @Override
    public void saveBlackWhite(String json) {
        if (StringUtils.isEmpty(json)) {
            return;
        }
        List<SystemBlackWhiteEntity> systemBlackWhiteEntities = JSON.parseArray(json, SystemBlackWhiteEntity.class);
        if (!ListUtils.isEmpty(systemBlackWhiteEntities)) {
            //黑名单
            SystemBlackWhiteEntity blackList = systemBlackWhiteEntities.stream().filter(systemBlackWhiteEntity -> systemBlackWhiteEntity.getType() == ConstantsDto.VISITRESTRICTION_TYPE_BLACK).collect(Collectors.toList()).get(0);
            //白名单
            SystemBlackWhiteEntity whiteList = systemBlackWhiteEntities.stream().filter(systemBlackWhiteEntity -> systemBlackWhiteEntity.getType() == ConstantsDto.VISITRESTRICTION_TYPE_WHITE).collect(Collectors.toList()).get(0);
            if (blackList.getUserNames().stream().filter(Objects::nonNull).anyMatch(userNameBlack -> whiteList.getUserNames().contains(userNameBlack))) {
                throw new IllegalArgumentException("黑白名单存在相同用户名");
            }
            if (blackList.getUserIps().stream().filter(Objects::nonNull).anyMatch(userIpBlack -> whiteList.getUserNames().contains(userIpBlack))) {
                throw new IllegalArgumentException("黑白名单存在相同用户IP");
            }
            if (blackList.getAssetIps().stream().filter(Objects::nonNull).anyMatch(assetIpBlack -> whiteList.getAssetIps().contains(assetIpBlack))) {
                throw new IllegalArgumentException("黑白名单存在相同设备IP");
            }

            //保存数据
            systemBlackWhiteEntities.stream() .filter(Objects::nonNull)
                    .forEach(systemBlackWhiteEntity -> {
//                        String createGuid =  SecurityUtils.getCurrentUser().getGuid();
                        String createGuid =  "1";
                        //用户名黑白名单
                        systemBlackWhiteEntity.getUserNames().stream().filter(Objects::nonNull)
                                .forEach(userName -> {
                                    User user = userMapper.findUserByUsername(userName);
                                    if (Objects.isNull(user)){
                                        throw new IllegalArgumentException("用户名："+userName+"不存在");
                                    }
                                    systemBlackWhiteMapper.insert(new SystemBlackWhite(systemBlackWhiteEntity.getType()== ConstantsDto.VISITRESTRICTION_TYPE_BLACK?SystemCons.SYSTEM_USER_BLACK:SystemCons.SYSTEM_USER_WHITE,user.getGuid(),userName,"","",createGuid, LocalDateTime.now()));
                                });
                        //用户ip黑白名单
                        systemBlackWhiteEntity.getUserIps().stream().filter(Objects::nonNull)
                                .forEach(userIp -> systemBlackWhiteMapper.insert(new SystemBlackWhite(systemBlackWhiteEntity.getType()== ConstantsDto.VISITRESTRICTION_TYPE_BLACK?SystemCons.SYSTEM_USER_IP_BLACK:SystemCons.SYSTEM_USER_IP_WHITE,"","",userIp,"", createGuid, LocalDateTime.now())));
                       //设备ip 黑白名单
                        systemBlackWhiteEntity.getAssetIps().stream().filter(Objects::nonNull)
                                .forEach(assetIp -> systemBlackWhiteMapper.insert(new SystemBlackWhite(systemBlackWhiteEntity.getType()== ConstantsDto.VISITRESTRICTION_TYPE_BLACK?SystemCons.SYSTEM_ASSET_IP_BLACK :SystemCons.SYSTEM_ASSET_IP_WHITE,"","","",assetIp,createGuid, LocalDateTime.now())));
                    });
        }
    }

    @Override
    public String getBlackWhite() {
//        SystemBlackWhiteEntity blackWhiteEntity = new SystemBlackWhiteEntity();
//        blackWhiteEntity.setType(ConstantsDto.VISITRESTRICTION_TYPE_BLACK);
//         systemBlackWhiteMapper.selectList(new QueryWrapper<SystemBlackWhite>().eq("type",SystemCons.SYSTEM_USER_BLACK))
//                 .stream().filter(Objects::nonNull).forEach(new Consumer<SystemBlackWhite>() {
//             @Override
//             public void accept(SystemBlackWhite systemBlackWhite) {
//
//             }
//         });


        return null;
    }
}
