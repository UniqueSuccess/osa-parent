package com.goldencis.osa.asset.mapper;

import com.goldencis.osa.asset.entity.AssetBs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 设备从表(B/S应用) Mapper 接口
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
public interface AssetBsMapper extends BaseMapper<AssetBs> {

    AssetBs selectResourceDetailByAssetId(@Param(value = "assetId") Integer assetId);
}
