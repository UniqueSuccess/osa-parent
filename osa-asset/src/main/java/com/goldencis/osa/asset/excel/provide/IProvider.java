package com.goldencis.osa.asset.excel.provide;

/**
 * 内容提供者
 * @param <IN>
 * @param <OUT>
 */
public interface IProvider<IN, OUT> {

    OUT provide(IN in) throws Exception;

}
