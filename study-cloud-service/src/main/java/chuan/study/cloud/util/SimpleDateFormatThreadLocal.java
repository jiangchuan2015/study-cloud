package chuan.study.cloud.util;

import chuan.study.cloud.common.Constants;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public class SimpleDateFormatThreadLocal {
    private static ThreadLocal<Map<String, SimpleDateFormat>> simpleDateFormatThreadLocal = new ThreadLocal<Map<String, SimpleDateFormat>>() {
        @Override
        protected synchronized Map<String, SimpleDateFormat> initialValue() {
            return new HashMap<>();
        }
    };

    public static SimpleDateFormat get() {
        return get(Constants.DATE_FORMAT);
    }

    public static SimpleDateFormat get(String dateFormat) {
        return simpleDateFormatThreadLocal.get().computeIfAbsent(dateFormat, SimpleDateFormat::new);
    }

    public static void clear() {
        simpleDateFormatThreadLocal.remove();
    }
}
