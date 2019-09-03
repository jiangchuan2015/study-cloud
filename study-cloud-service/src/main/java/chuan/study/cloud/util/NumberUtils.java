package chuan.study.cloud.util;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {
    /**
     * Null == value
     *
     * @param value 需要检查的数字
     * @return 检查的数据为 NULL
     */
    public static boolean isNull(Number value) {
        return null == value;
    }

    /**
     * Null != value
     *
     * @param value 需要检查的数字
     * @return 检查的数据不是 NULL
     */
    public static boolean isNotNull(Number value) {
        return null != value;
    }

    /**
     * Null != value && value > 0
     *
     * @param value
     * @return
     */
    public static boolean isPositive(Number value) {
        if (isNotNull(value)) {
            if (value instanceof Integer) {
                return value.intValue() > 0;
            } else if (value instanceof Long) {
                return value.longValue() > 0;
            } else if (value instanceof Byte) {
                return value.byteValue() > 0;
            } else if (value instanceof Double) {
                return value.doubleValue() > 0;
            } else if (value instanceof Float) {
                return value.floatValue() > 0;
            } else if (value instanceof Short) {
                return value.shortValue() > 0;
            }
        }
        return false;
    }

    /**
     * Null == value || value < 1
     *
     * @param value
     * @return
     */
    public static boolean isNotPositive(Number value) {
        if (isNull(value)) {
            return true;
        }
        if (value instanceof Integer) {
            return value.intValue() < 1;
        } else if (value instanceof Long) {
            return value.longValue() < 1;
        } else if (value instanceof Byte) {
            return value.byteValue() < 1;
        } else if (value instanceof Double) {
            return value.doubleValue() < 1;
        } else if (value instanceof Float) {
            return value.floatValue() < 1;
        } else if (value instanceof Short) {
            return value.shortValue() < 1;
        }
        return true;
    }
}
