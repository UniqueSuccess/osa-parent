package com.goldencis.osa.task.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangmc
 * @since 2019-01-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_task_asset_email")
public class TaskAssetEmail extends Model<TaskAssetEmail> {

    private static final long serialVersionUID = 1L;

    private String taskId;

    private String email;

    public TaskAssetEmail() {
    }

    public TaskAssetEmail(String taskId, String email) {
        this.taskId = taskId;
        this.email = email;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
