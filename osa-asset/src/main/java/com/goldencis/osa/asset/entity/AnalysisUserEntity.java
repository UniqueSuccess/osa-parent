package com.goldencis.osa.asset.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AnalysisUserEntity extends Model<AnalysisUserEntity> {
    /**
     * {"userType": 1,"userId": "","usergroupId": ""}
     */
    /**
     * 授权设备类型 1用户，2用户组
     */
    private Integer userType;
    /**
     * 授权设备组
     */
    private String userId;
    /**
     * 授权设备账号
     */
    private String usergroupId;

    @Override
    protected Serializable pkVal() {
        return this.userType;
    }
}
