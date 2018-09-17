package com.usoftchina.chain.dashboard.util;

/**
 * @author yingp
 * @date 2018/9/14
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Exception> {
    void accept(T t, U u) throws E;
}
