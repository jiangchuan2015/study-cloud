package chuan.study.cloud.web.annotation;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public enum AuthPolicy {
    /**
     * 必需要有权限码
     */
    REQUIRED,

    /**
     * 忽略权限检查
     */
    IGNORED
}
