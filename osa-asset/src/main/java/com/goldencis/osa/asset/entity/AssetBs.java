package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.goldencis.osa.asset.resource.AssetResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 设备从表(B/S应用)
 * </p>
 *
 * @author limingchao
 * @since 2018-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_asset_bs")
public class AssetBs extends Model<AssetBs> implements AssetResource {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id
     */
    private Integer assetId;

    /**
     * 应用程序发布器
     */
    private Integer publish;

    /**
     * 运维工具
     */
    private Integer operationTool;

    /**
     * 应用密码代填
     */
    private Integer fillOut;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单提交方式
     */
    private String formCommitMode;

    /**
     * 口令属性
     */
    private String command;

    /**
     * 登录url
     */
    private String loginUrl;

    @Override
    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    @Override
    public Integer getAssetId() {
        return this.assetId;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
