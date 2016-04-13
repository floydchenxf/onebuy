package com.floyd.onebuy.aync;

/**
 * Created by floyd on 16-2-1.
 */
public interface Func2<T1, T2, R> {
    R call(T1 t1, T2 t2);
}