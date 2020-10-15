package com.goldencis.osa.asset.excel;

import reactor.util.annotation.Nullable;

/**
 * 导入
 */
public interface IImport<T> {

    String sheetName();

    void in(T t) throws Exception;

    interface Callback<T> {
        /**
         * 导入之前,先清理数据库中之前的数据
         */
        void cleanup();

        /**
         * 是否允许覆盖
         * @return
         */
        boolean allowCover();

        /**
         * 保存到数据库中
         * @param name 名称
         * @param t 上级部门
         */
        void save(String name, @Nullable T t);

        /**
         * 通过名称查找指定的部门
         * @param name
         * @return
         */
        @Nullable
        T findByName(String name);

    }

    /**
     * 处理旧数据,在用户选择全量更新时,会将之前的数据缓存起来,导入完成后,将缓存的数据删除
     * 如果导入出错,则将缓存的数据恢复
     */
    interface OldDataHandler {
        /**
         * 缓存
         */
        void cache();

        /**
         * 清理缓存
         */
        void cleanup();

        /**
         * 恢复缓存
         */
        void restore();

        /**
         * 是否启用
         * @return
         */
        boolean enable();

        /**
         * 配置开关
         * @param enable
         */
        void setEnable(boolean enable);
    }
}
