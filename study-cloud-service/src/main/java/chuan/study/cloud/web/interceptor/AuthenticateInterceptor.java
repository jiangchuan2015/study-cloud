package chuan.study.cloud.web.interceptor;

import chuan.study.cloud.common.Constants;
import chuan.study.cloud.pojo.domain.ApiOut;
import chuan.study.cloud.pojo.domain.user.UserContext;
import chuan.study.cloud.pojo.domain.user.UserContextThreadLocal;
import chuan.study.cloud.pojo.enums.ResponseCodeEnum;
import chuan.study.cloud.util.ConvertUtils;
import chuan.study.cloud.util.NumberUtils;
import chuan.study.cloud.web.annotation.AuthPolicy;
import chuan.study.cloud.web.annotation.Authenticate;
import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
@AllArgsConstructor
public class AuthenticateInterceptor extends HandlerInterceptorAdapter implements HandlerInterceptor {
    private final StringRedisTemplate redisTemplate;

    /**
     * 令牌缓存，the key is token, the value is it's status
     */
    private static Cache<String, Boolean> TOKEN_CACHE = Caffeine.newBuilder()
            .initialCapacity(20).maximumSize(3_000)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // API 参数
        String apiUrl = request.getRequestURI();

        // ================================== 验证 权限代码 ==================================
        // 不需要拦截 AuthPolicy.IGNORED & 权限代码为空的
        Method handlerMethod = ((HandlerMethod) handler).getMethod();
        Class handlerClass = handlerMethod.getDeclaringClass();
        String signature = handlerClass.getName() + '#' + handlerMethod.getName();

        Authenticate authenticate = handlerMethod.getAnnotation(Authenticate.class);
        if (null == authenticate) {
            authenticate = (Authenticate) handlerClass.getAnnotation(Authenticate.class);
        }

        // 如果没有配置必须登录，则直接放行
        if (null != authenticate && AuthPolicy.IGNORED.equals(authenticate.policy())) {
            log.info("对({}-{})不需要登录", signature, apiUrl);
            return true;
        }

        // 如果是 Debug, 则忽略授权令牌, 权限的检查
        if (ConvertUtils.toBoolean(request.getParameter(Constants.PARA_DEBUG), false)) {
            return true;
        }

        // 检查授权令牌
        UserContext context = UserContextThreadLocal.get();
        if (!isValid(context, apiUrl)) {
            log.warn("Host({}), URL({}), 授权令牌({})无效。", context.getHost(), apiUrl, context.getToken());
            ajaxReturn(response, ResponseCodeEnum.TOKEN_EXPIRED, context.getToken());
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 释放用户信息，防止内存泄露
        UserContextThreadLocal.remove();
    }


    private boolean isValid(final UserContext userContext, final String url) {
        if (Objects.isNull(userContext)) {
            return false;
        }

        if (NumberUtils.isNotPositive(userContext.getUserId())) {
            log.warn("Host({}), URL({}), 授权令牌({})无效。", userContext.getHost(), url, userContext.getToken());
            return false;
        }

        if (NumberUtils.isNotPositive(userContext.getUserType())) {
            log.warn("Host({}), URL({}), 授权令牌({})无效。", userContext.getHost(), url, userContext.getToken());
            return false;
        }

        if (userContext.isTokenExpired()) {
            log.warn("Host({}), URL({}), 授权令牌({})已经过期。", userContext.getHost(), url, userContext.getToken());
            return false;
        }

        return true;
    }


    /**
     * Ajax 返回错误信息
     *
     * @param response     HttpServletResponse
     * @param responseCode ResponseCodeEnum
     * @param message      提示错误消息
     * @throws IOException IOException
     */
    private void ajaxReturn(HttpServletResponse response, ResponseCodeEnum responseCode, String message) throws IOException {
        responseCode = Optional.ofNullable(responseCode).orElse(ResponseCodeEnum.TOKEN_EXPIRED);

        ApiOut apiOut = ApiOut.newResponse(responseCode, message);
        response.setCharacterEncoding(Constants.DEFAULT_CHAR_SET);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();
        out.print(JSON.toJSONString(apiOut));
        out.flush();
    }
}

