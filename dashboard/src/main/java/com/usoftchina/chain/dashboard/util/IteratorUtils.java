package com.usoftchina.chain.dashboard.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author yingp
 * @date 2018/9/14
 */
public class IteratorUtils {
    /**
     * 在Lambda表达式中处理Checked异常
     *
     * @param throwingConsumer
     * @param <T>
     * @return
     */
    public static <T> Consumer<T> throwingConsumer(ThrowingConsumer<T, Exception> throwingConsumer) {
        return t -> {
            try {
                throwingConsumer.accept(t);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
    /**
     * 在Lambda表达式中处理Checked异常
     *
     * @param throwingConsumer
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> BiConsumer<T, U> throwingConsumer(ThrowingBiConsumer<T, U, Exception> throwingConsumer) {
        return (t, u) -> {
            try {
                throwingConsumer.accept(t, u);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
