package com.goldencis.osa.asset.excel.domain;

import com.goldencis.osa.asset.entity.Asset;
import com.goldencis.osa.asset.entity.AssetBs;
import com.goldencis.osa.asset.entity.AssetCs;
import com.goldencis.osa.asset.entity.AssetDb;
import com.goldencis.osa.asset.excel.annotation.Export;
import com.goldencis.osa.asset.excel.annotation.Import;
import com.goldencis.osa.asset.resource.AssetResource;
import lombok.Data;

import java.util.Objects;

/**
 * 设备导入导出条目
 *
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-13 14:03
 **/
@Data
public class AssetItem {
    @Export(desc = "设备类型", order = 0)
    @Import(desc = "设备类型", order = 0)
    private String assetType;
    @Export(desc = "设备名称", order = 1)
    @Import(desc = "设备名称", order = 1)
    private String assetName;
    @Export(desc = "设备IP", order = 2)
    @Import(desc = "设备IP", order = 2)
    private String assetIp;
    @Export(desc = "设备组", order = 3)
    @Import(desc = "设备组", order = 3)
    private String assetGroupName;
    @Export(desc = "编码(UTF-8/GBK)", order = 4)
    @Import(desc = "编码(UTF-8/GBK)", order = 4, nullable = true)
    private String encode;
    @Export(desc = "应用程序发布器", order = 5)
    @Import(desc = "应用程序发布器", order = 5, nullable = true)
    private String publish;
    @Export(desc = "连接工具", order = 6)
    @Import(desc = "连接工具", order = 6, nullable = true)
    private String operationTool;
    @Export(desc = "URL", order = 7)
    @Import(desc = "URL", order = 7, nullable = true)
    private String url;
    @Export(desc = "数据库名称", order = 8)
    @Import(desc = "数据库名称", order = 8, nullable = true)
    private String dbName;
    @Export(desc = "端口", order = 9)
    @Import(desc = "端口", order = 9, nullable = true)
    private String port;
    @Export(desc = "管理账号", order = 10)
    @Import(desc = "管理账号", order = 10, nullable = true)
    private String adminAccount;
    @Export(desc = "管理密码", order = 11)
    @Import(desc = "管理密码", order = 11, nullable = true)
    private String adminPassword;
    @Export(desc = "备注", order = 12)
    @Import(desc = "备注", order = 12, nullable = true)
    private String remark;
    /**
     * 是,或者 否
     */
    @Export(desc = "是否应用程序发布器", order = 13)
    @Import(desc = "是否应用程序发布器", order = 13, nullable = true)
    private String isPublish;

    public AssetItem() {
    }

    public AssetItem(Asset a) {
        this.setAssetType(a.getTypeName());
        this.setAssetName(a.getName());
        this.setAssetIp(a.getIp());
        this.setAssetGroupName(a.getGroupName());
        this.setEncode(a.getEncode());
        this.setRemark(a.getRemark());
        this.setAdminAccount(a.getAccount());
        this.setAdminPassword(a.getPassword());
        AssetResource extra = a.getExtra();
        Publish publish = Publish.matchByValue(a.getIsPublish());
        if (Objects.nonNull(publish)) {
            this.setIsPublish(publish.getContent());
        }
        if (Objects.nonNull(extra)) {
            if (extra instanceof AssetCs) {
                AssetCs cs = (AssetCs) extra;
                this.setPublish(cs.getPublishName());
                this.setOperationTool(cs.getOperationToolName());
            } else if (extra instanceof AssetBs) {
                AssetBs bs = (AssetBs) extra;
                this.setUrl(bs.getLoginUrl());
            } else if (extra instanceof AssetDb) {
                AssetDb db = (AssetDb) extra;
                this.setPublish(db.getPublishName());
                this.setOperationTool(db.getOperationToolName());
                this.setDbName(db.getDbName());
                this.setPort(String.valueOf(db.getPort()));
            } else {
                throw new IllegalArgumentException("AssetResource类型错误:" + extra);
            }
        }
    }

}
