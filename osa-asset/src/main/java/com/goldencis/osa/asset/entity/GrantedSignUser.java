package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 单点登录获取用户 授权的所有设备账号
 * </p>
 *
 * @author limingchao
 * @since 2018-11-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GrantedSignUser extends Model<GrantedSignUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 授权设备组id
     */
    private Integer assetgroupId;

    /**
     * 授权设备组名称
     */
    private String assetgroupName;

    /**
     * 授权设备id
     */
    private Integer assetId;

    /**
     * 授权设备名称
     */
    private String assetName;

    /**
     * 授权设备ip
     */
    private String assetIp;

    /**
     * 授权账户id
     */
    private Integer accountId;

    /**
     * 授权账户名
     */
    private String accountName;

    /**
     * 授权设备类型id
     */
    private Integer assettypeId;

    /**
     * 授权设备类型名称
     */
    private String assettypeName;

    /**
     * 授权设备类型图片
     */
    private String assettypeIcon;

    @Override
    protected Serializable pkVal() {
        return this.accountId;
    }

}
