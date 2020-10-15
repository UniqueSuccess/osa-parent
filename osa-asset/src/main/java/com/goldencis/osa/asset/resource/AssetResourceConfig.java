package com.goldencis.osa.asset.resource;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.asset.resource.domain.IRelation;
import com.goldencis.osa.core.config.ResourceMapperMapConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-01 14:03
 **/
@Configuration
public class AssetResourceConfig {

    Logger logger = Logger.getLogger(ResourceMapperMapConfig.class);

    @Autowired
    private List<IResourceService<? extends AssetResource>> services;

    @Bean
    public Map<Integer, IResourceService<? extends AssetResource>> assetResourceMapperMap() {
        Map<Integer, IResourceService<? extends AssetResource>> mapperMap = new HashMap<>();
        for (IResourceService<? extends AssetResource> service : services) {
            AssetResourceType resourceType = service.resourceType();
            mapperMap.put(resourceType.getValue(), service);
        }
        return mapperMap;
    }

    @Autowired
    private List<? extends IRelation> relations;

    @Bean
    public Map<Integer, ? extends IRelation> relationMap() {
        List<? extends IRelation> list = relations;
        Map<Integer, IRelation> map = new HashMap<>(list.size());
        for (IRelation relation : list) {
            map.put(relation.typeId(), relation);
        }
        return map;
    }

}
