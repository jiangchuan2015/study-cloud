package chuan.study.cloud.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authenticate {
    /**
     * 需要的权限字符
     */
    String[] value() default {};

    /**
     * 认证策略
     */
    AuthPolicy policy() default AuthPolicy.REQUIRED;
}
