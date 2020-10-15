package com.goldencis.osa.asset.resource.impl;

import com.goldencis.osa.asset.entity.AssetType;
import com.goldencis.osa.asset.resource.AssetResourceType;
import com.goldencis.osa.asset.resource.IAssetTypeParser;
import com.goldencis.osa.asset.resource.domain.IRelation;
import com.goldencis.osa.asset.service.IAssetTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.util.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 解析设备类型
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-01 14:28
 **/
@Component(value = "assetTypeParserImpl")
public class AssetTypeParserImpl implements IAssetTypeParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Map<Integer, ? extends IRelation> relationMap;
    @Autowired
    private IAssetTypeService assetTypeService;

    @Nullable
    @Override
    public AssetResourceType parse(@NotNull Integer assetType) {
        AssetType type;
        try {
            type = assetTypeService.getMostSuperiorAssetTypeById(assetType);
        } catch (Exception e) {
            logger.error("t_asset_type表中没有对应信息,assetType : " + assetType, e);
            return null;
        }
        return relationMap.get(type.getId()).resourceType();
    }


}
