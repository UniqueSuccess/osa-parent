package com.goldencis.osa.core.mapper;

import com.goldencis.osa.core.params.UkeyParams;
import com.goldencis.osa.core.entity.Ukey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * USBKey Mapper 接口
 * </p>
 *
 * @author wangmc
 * @since 2019-01-28
 */
public interface UkeyMapper extends BaseMapper<Ukey> {

    Integer getUkeyCount(UkeyParams params);

    List<Ukey> getUkeyListInPage(UkeyParams params);
}
