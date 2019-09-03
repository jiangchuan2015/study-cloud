package chuan.study.cloud.util;


import chuan.study.cloud.exception.BusinessException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public final class ConvertUtils {
    /**
     * 将字符串转换成数字(Integer)，如果字符串不合法将返回默认值
     *
     * @param value        需要转换成数字的字符串
     * @param defaultValue 默认值
     * @return 转换后的数字
     */
    public static Integer toInteger(String value, Integer defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    /**
     * 将字符串转换成数字(Integer)
     *
     * @param value 需要转换成数字的字符串
     * @return 转换后的数字
     * @deprecated {@link NumberUtils#toInt(String)}
     */
    public static Integer toInteger(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 将字符串转换成数字(Long)，如果字符串不合法将返回默认值
     *
     * @param value        需要转换成数字的字符串
     * @param defaultValue 默认值
     * @return 转换后的数字
     */
    public static Long toLong(String value, Long defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换成数字(Long)
     *
     * @param value 需要转换成数字的字符串
     * @return 转换后的数字
     */
    public static Long toLong(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 字符串转int数字
     *
     * @param value
     * @return
     */
    public static Integer toInt(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer toInt(String value, Integer defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换成数字(Float)，如果字符串不合法将返回默认值
     *
     * @param value        需要转换成数字的字符串
     * @param defaultValue 默认值
     * @return 转换后的数字
     */
    public static Float toFloat(String value, Float defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        try {
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    /**
     * 将字符串转换成数字(double)，如果字符串不合法将返回默认值
     *
     * @param value        需要转换成数字的字符串
     * @param defaultValue 默认值
     * @return 转换后的数字
     */
    public static Double toDouble(String value, Double defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换成数字(BigDecimal)，如果字符串不合法将返回默认值
     *
     * @param value        需要转换成数字的字符串
     * @param defaultValue 默认值
     * @return 转换后的数字
     */
    public static BigDecimal toBigDecimal(String value, BigDecimal defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换成布尔值(boolean)，如果字符串不合法将返回默认值
     *
     * @param value        需要转换成布尔值的字符串
     * @param defaultValue 默认值
     * @return 转换后的布尔值
     */
    public static boolean toBoolean(String value, boolean defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        if ("on".equals(value)) {
            return true;
        }
        if ("off".equals(value)) {
            return false;
        }
        if ("1".equals(value)) {
            return true;
        }
        if ("0".equals(value)) {
            return false;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换成布尔值(Boolean)，如果字符串不合法将返回默认值
     *
     * @param value        需要转换成布尔值的字符串
     * @param defaultValue 默认值
     * @return 转换后的布尔值
     */
    public static Boolean toBoolean(String value, Boolean defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        if ("on".equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        }
        if ("off".equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        }
        if ("1".equals(value)) {
            return Boolean.TRUE;
        }
        if ("0".equals(value)) {
            return Boolean.FALSE;
        }
        try {
            return Boolean.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 转换日期为字符串
     *
     * @param value 需要转换的日期
     * @return 转换出来的日期字符串
     */
    public static String toString(Date value) {
        if (value == null) {
            return "";
        }
        return SimpleDateFormatThreadLocal.get().format(value);
    }

    /**
     * 转换日期为字符串
     *
     * @param value  需要转换的日期
     * @param format 日期转换格式
     * @return 转换出来的日期字符串
     */
    public static String toString(Date value, String format) {
        if (value == null) {
            return "";
        }
        if (StringUtils.isBlank(format)) {
            return toString(value);
        }
        return SimpleDateFormatThreadLocal.get(format).format(value);
    }


    /**
     * 将字符串转换成日期
     *
     * @param value 需要转换的字符串
     * @return 转换出来的日期
     */
    public static Date toDate(String value) {
        if (value.indexOf(':') == -1) {
            value += " 00:00:00";
        }
        try {
            return SimpleDateFormatThreadLocal.get().parse(value);
        } catch (ParseException e) {
            throw new BusinessException("日期(" + value + ")无效!");
        }
    }

    /**
     * 将字符串转换成日期
     *
     * @param value  需要转换的字符串
     * @param format 日期转换格式
     * @return 转换出来的日期
     */
    public static Date toDate(String value, String format) {
        if (StringUtils.isBlank(format)) {
            return toDate(value);
        }
        try {
            return SimpleDateFormatThreadLocal.get(format).parse(value);
        } catch (ParseException e) {
            throw new BusinessException("日期(" + value + ")无效!");
        }
    }

    /**
     * 将字符串转换成日期
     *
     * @param value        需要转换的字符串
     * @param format       日期转换格式
     * @param defaultValue 默认日期
     * @return 转换出来的日期
     */
    public static Date toDate(String value, String format, Date defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return StringUtils.isBlank(format) ? toDate(value) : toDate(value, format);
        } catch (BusinessException e) {
            return defaultValue;
        }
    }
}
