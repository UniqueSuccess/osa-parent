package com.goldencis.osa.core.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.core.entity.Resource;
import com.goldencis.osa.core.utils.ResourceType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将资源相关的mapper接口，由List，转化为Map。
 * 方便在权限service中根据资源的type动态调用资源的mapper，而不会收到模块引用的制约，方便在后期开发中添加新的资源类型。
 * key为：每个mapper对应的ResourceType的value值。
 * value为：mapper。
 * Created by limingchao on 2018/10/12.
 */
@Configuration
public class ResourceMapperMapConfig {

    Logger logger = Logger.getLogger(ResourceMapperMapConfig.class);

    @Autowired
    private List<BaseMapper<? extends Resource>> mapperList;

    @Bean
    public Map<Integer, BaseMapper<? extends Resource>> resourceMapperMap() {
        Map<Integer, BaseMapper<? extends Resource>> mapperMap = new HashMap<>();
        for (BaseMapper<? extends Resource> resourceMapper : mapperList) {
            String mapperName = resourceMapper.getClass().getInterfaces()[0].getSimpleName();

            if (mapperName.endsWith("Mapper")) {
                String name = null;
                try {
                    name = mapperName.substring(0, mapperName.lastIndexOf("Mapper"));
                    ResourceType resourceType = ResourceType.valueOf(name.toUpperCase());
                    mapperMap.put(resourceType.getValue(), resourceMapper);
                } catch (IllegalArgumentException e) {
                    logger.warn("Mismatch Resource Type, name is : " + name);
                } catch (Exception e) {
                    logger.error("Resource Type Match Failed!");
                }
            }

        }
        return mapperMap;
    }
}
