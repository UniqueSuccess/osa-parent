package com.goldencis.osa.system.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *系统配置--> 管控平台--> 黑白名单 解析bean
 * </p>
 *
 * @author wangtt
 * @since 2018-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SystemBlackWhiteEntity extends Model<SystemBlackWhiteEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 1用户黑名单，2用户白名单，3 设备黑名单，4设备白名单
     */
    private Integer type;

    /**
     * 用户名
     */
    private List<String> userNames;

    /**
     * 限定用户的ip、ip段
     */
    private List<String> userIps;

    /**
     * 限定设备的ip、ip段
     */
    private List<String> assetIps;

    @Override
    protected Serializable pkVal() {
        return this.type;
    }

}
