package com.goldencis.osa.asset.mapper;

import com.goldencis.osa.asset.entity.AssetDb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 设备从表(数据库类型设备) Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
public interface AssetDbMapper extends BaseMapper<AssetDb> {

    AssetDb selectResourceDetailByAssetId(@Param(value = "assetId") Integer assetId);

}
