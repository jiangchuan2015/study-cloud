package chuan.study.cloud.pojo.domain.user;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public class UserContextThreadLocal {
    private static final InheritableThreadLocal<UserContext> THREAD_LOCAL = new InheritableThreadLocal<UserContext>() {
        @Override
        protected UserContext initialValue() {
            return UserContext.builder().build();
        }
    };

    /**
     * 为每个请求设置用户信息
     *
     * @return 用户信息
     */
    public static UserContext get() {
        return THREAD_LOCAL.get();
    }

    /**
     * 为每个请求设置用户信息
     */
    public static void set(UserContext userContext) {
        THREAD_LOCAL.set(userContext);
    }

    /**
     * 释放用户信息，防止内存泄露
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
