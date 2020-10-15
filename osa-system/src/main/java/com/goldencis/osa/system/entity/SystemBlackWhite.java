package com.goldencis.osa.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *系统配置--> 管控平台--> 黑白名单
 * </p>
 *
 * @author limingchao
 * @since 2018-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_system_black_white")
public class SystemBlackWhite extends Model<SystemBlackWhite> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 1用户黑名单，2用户白名单，3 设备黑名单，4设备白名单
     */
    private Integer type;

    /**
     * 用户guid
     */
    private String userGuid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 限定用户的ip、ip段
     */
    private String userIp;

    /**
     * 限定设备的ip、ip段
     */
    private String assetIp;

    /**
     * 创建人guid
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public SystemBlackWhite() {
    }

    public SystemBlackWhite( Integer type, String userGuid, String username, String userIp, String assetIp, String createBy, LocalDateTime createTime) {
        this.type = type;
        this.userGuid = userGuid;
        this.username = username;
        this.userIp = userIp;
        this.assetIp = assetIp;
        this.createBy = createBy;
        this.createTime = createTime;
    }
}
