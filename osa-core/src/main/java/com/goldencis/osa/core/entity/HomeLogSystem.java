package com.goldencis.osa.core.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 系统日志
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HomeLogSystem extends Model<HomeLogSystem> {

    private static final long serialVersionUID = 1L;

    /**
     * 活跃账号top5
     */
    private List<LogSystem> userTop;

    /**
     * 资源运维top5
     */
    private List<LogSystem> assetTop;
    /**
     * 最近运维记录
     */
    private List<LogSystem> logOperation;


    @Override
    protected Serializable pkVal() {
        return 1;
    }

}
