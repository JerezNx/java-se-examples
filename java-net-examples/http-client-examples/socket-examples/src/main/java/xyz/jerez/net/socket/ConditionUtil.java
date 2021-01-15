package xyz.jerez.net.socket;

import java.util.function.Predicate;

/**
 * @author liqilin
 * @since 2020/11/24 14:14
 */
public class ConditionUtil {

    /**
     * 如果满足条件，则返回值，否则返回默认值
     * @param t 待判断值
     * @param condition 判断条件
     * @param defaultValue 默认值
     * @param <T> 值类型
     * @return 最终值
     */
    public static <T> T getDefaultIfCondition(T t, Predicate<T> condition, T defaultValue) {
        return condition.test(t) ? t : defaultValue;
    }

}
