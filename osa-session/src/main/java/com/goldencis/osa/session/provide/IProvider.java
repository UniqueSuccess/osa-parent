package com.goldencis.osa.session.provide;

public interface IProvider<IN, OUT> {

    OUT get(IN in) throws Exception;

}
