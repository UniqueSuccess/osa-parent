package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AnalysisGrantedEntity   extends Model<AnalysisGrantedEntity> {
    /**
     * {"grantedType": 1,"assetgroups": "","assetaccounts": "asset-3,asset-4"}
     */
    /**
     * 授权设备类型  1设备组，2设备，3设备账户
     */
    private Integer grantedType;
    /**
     * 授权设备组
     */
    private String assetgroups;
    /**
     * 授权设备账号
     */
    private String assetaccounts;

    @Override
    protected Serializable pkVal() {
        return this.grantedType;
    }
}
