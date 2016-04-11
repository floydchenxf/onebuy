package com.floyd.onebuy.aync;

/**
 * Created by floyd on 16-1-8.
 */
public interface Processor<T> {
    /**
     * 过程处理
     *
     * @param t
     */
    public void doProcess(T t);
}
