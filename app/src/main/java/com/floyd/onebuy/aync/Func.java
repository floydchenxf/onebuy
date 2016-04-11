package com.floyd.onebuy.aync;

/**
 * Created by floyd on 15-11-19.
 */
public interface Func<T, R> {
    R call(T t);
}
