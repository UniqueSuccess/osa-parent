package com.goldencis.osa.asset.excel;

import lombok.Data;

import java.util.List;

/**
 * 导出
 */
public interface IExport<T> {

    /**
     * sheet页名称
     * @return
     */
    String sheetName();

    void export(T in) throws Exception;

    @Data
    class Builder<V, P> {
        private V t;
        private List<P> list;
    }
}
