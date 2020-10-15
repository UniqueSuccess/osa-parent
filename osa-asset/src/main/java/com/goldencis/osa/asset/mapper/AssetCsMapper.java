package com.goldencis.osa.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goldencis.osa.asset.entity.AssetCs;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 设备从表(C/S应用) Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-11-15
 */
public interface AssetCsMapper extends BaseMapper<AssetCs> {

    AssetCs selectResourceDetailByAssetId(@Param(value = "assetId") Integer assetId);

}
