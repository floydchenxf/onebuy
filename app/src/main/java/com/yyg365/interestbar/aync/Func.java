package com.yyg365.interestbar.aync;

/**
 * Created by floyd on 15-11-19.
 */
public interface Func<T, R> {
    R call(T t);
}
