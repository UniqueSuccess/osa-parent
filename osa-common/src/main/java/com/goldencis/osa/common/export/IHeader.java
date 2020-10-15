package com.goldencis.osa.common.export;

/**
 * 头部每一个单元格
 */
public interface IHeader extends Comparable<IHeader> {
    /**
     * 单元格内容
     *
     * @return
     */
    String content();

    /**
     * 排序
     * @return
     */
    int order();
}
