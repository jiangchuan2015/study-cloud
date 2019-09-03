package chuan.study.cloud.web.interceptor;

import chuan.study.cloud.common.Constants;
import chuan.study.cloud.pojo.domain.user.SerializableToken;
import chuan.study.cloud.pojo.domain.user.UserContext;
import chuan.study.cloud.pojo.domain.user.UserContextThreadLocal;
import chuan.study.cloud.util.ConvertUtils;
import chuan.study.cloud.util.HostUtils;
import chuan.study.cloud.util.UserTokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
@AllArgsConstructor
public class UserContextInterceptor extends HandlerInterceptorAdapter {
    private final UserTokenUtils userTokenUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String apiUrl = request.getRequestURI();

        // 调试模式
        if (ConvertUtils.toBoolean(request.getParameter(Constants.PARA_DEBUG), false)) {
            log.info("【调试模式】不解析Token, 请求URL: {}", apiUrl);
            return true;
        }

        // 授权令牌
        String token = StringUtils.trimToNull(request.getHeader(Constants.TOKEN_PARA_NAME));
        if (StringUtils.isNotBlank(token)) {
            UserContext context = userTokenUtils.getUserContext(token);
            SerializableToken serializableToken;
            if (null == context && null != (serializableToken = userTokenUtils.getSerializableToken(token))) {
                context = UserContext.builder()
                        .userId(serializableToken.getUserId())
                        .userType(serializableToken.getUserType())
                        .token(token).tokenExpired(true)
                        .build();
            }
            UserContextThreadLocal.set(Optional.ofNullable(context).orElse(new UserContext()));
        }

        // 请求者IP
        UserContextThreadLocal.get().setHost(HostUtils.getRequestIp(request));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextThreadLocal.remove();
    }
}
