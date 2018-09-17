package com.usoftchina.chain.dashboard.util;

/**
 * @author yingp
 * @date 2018/9/14
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;
}
