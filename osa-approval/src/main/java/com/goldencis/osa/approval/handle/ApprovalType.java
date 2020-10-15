package com.goldencis.osa.approval.handle;

import com.goldencis.osa.common.constants.ConstantsDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审批类型
 */
public enum ApprovalType {
    /**
     * 添加命令
     */
    COMMAND_ADD(ConstantsDto.APPROVAL_COMMAND_ADD, Differ.ADD),
    /**
     * 添加授权
     */
    GRANTED_ADD(ConstantsDto.APPROVAL_GRANTED_ADD, Differ.ADD),
    /**
     * 删除授权
     */
    GRANTED_DELETE(ConstantsDto.APPROVAL_GRANTED_DELETE, Differ.DELETE),
    /**
     * 删除设备
     */
    DELETE_ASSET(ConstantsDto.APPROVAL_GRANTED_DELETE_ASSET, Differ.DELETE),
    /**
     * 删除账号
     */
    DELETE_ACCOUNT(ConstantsDto.APPROVAL_GRANTED_DELETE_ACCOUNT, Differ.DELETE),
    /**
     * 删除设备组
     */
    DELETE_ASSET_GROUP(ConstantsDto.APPROVAL_GRANTED_DELETE_ASSET_GROUP, Differ.DELETE);

    /**
     * 标记,区分
     */
    private int mark;
    private Differ differ;

    ApprovalType(int mark, Differ differ) {
        this.mark = mark;
        this.differ = differ;
    }

    public int getMark() {
        return mark;
    }

    public Differ getDiffer() {
        return differ;
    }

    /**
     * 获取所有的审批类型
     * @return
     */
    public static List<Integer> getAllTypeMark() {
        return getAllType().stream()
                .map(ApprovalType::getMark)
                .collect(Collectors.toList());
    }

    public static List<Integer> getTypeMarkByDiffer(ApprovalType.Differ differ) {
        return getTypeByDiffer(differ).stream()
                .map(ApprovalType::getMark)
                .collect(Collectors.toList());
    }

    public static List<ApprovalType> getTypeByDiffer(ApprovalType.Differ differ) {
        return getAllType().stream()
                .filter(item -> item.getDiffer().equals(differ))
                .collect(Collectors.toList());
    }

    public static List<ApprovalType> getAllType() {
        return Arrays.asList(ApprovalType.values());
    }

    /**
     * 多种审批类型的一种归类方式?
     * 或者说是对审批类型比较笼统的区分
     */
    public enum Differ {
        /**
         * 添加
         */
        ADD(1),
        /**
         * 删除
         */
        DELETE(2);

        private int value;

        Differ(int value) {
            this.value = value;
        }
    }

}
