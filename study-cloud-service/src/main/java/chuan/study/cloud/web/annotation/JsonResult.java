package chuan.study.cloud.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(JsonResults.class)
public @interface JsonResult {
    /**
     * JSON 序列化的对象
     */
    Class<?> type();

    /**
     * JSON 返回时包含的字段
     */
    String[] include() default {};

    /**
     * JSON 返回时排除的字段
     */
    String[] exclude() default {};

    /**
     * 是否短日期格式, 默认 false
     * true: yyyy-MM-dd
     * false: yyyy-MM-dd HH:mm:ss
     */
    boolean shortDateFormat() default false;
}
