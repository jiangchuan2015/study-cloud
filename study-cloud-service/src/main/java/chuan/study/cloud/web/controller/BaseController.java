package chuan.study.cloud.web.controller;

import chuan.study.cloud.common.Constants;
import chuan.study.cloud.pojo.domain.user.UserContext;
import chuan.study.cloud.pojo.domain.user.UserContextThreadLocal;
import chuan.study.cloud.util.ConvertUtils;
import chuan.study.cloud.util.HostUtils;
import chuan.study.cloud.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
public abstract class BaseController {
    @Autowired
    protected HttpServletRequest httpRequest;


    /**
     * 根据请求获取IP
     *
     * @return 请求IP
     */
    protected String getRequestHost() {
        return getRequestHost(getRequest());
    }

    /**
     * 根据请求获取IP
     *
     * @param request HttpServletRequest
     * @return 请求IP
     */
    protected String getRequestHost(HttpServletRequest request) {
        String host = Optional.ofNullable(UserContextThreadLocal.get()).map(UserContext::getHost).orElse(StringUtils.EMPTY);
        if (StringUtils.isNotBlank(host)) {
            return host;
        }
        request = Optional.ofNullable(request).orElse(httpRequest);
        return null == request ? StringUtils.EMPTY : HostUtils.getRequestIp(request);
    }

    /**
     * 将URL上的字符串进行解码
     *
     * @param searchTerm 查询的字条串
     * @return 解码后的字符串
     */
    protected String decodeString(String searchTerm) {
        if (StringUtils.isNotBlank(searchTerm)) {
            try {
                return URLDecoder.decode(searchTerm.trim(), Constants.DEFAULT_CHAR_SET);
            } catch (UnsupportedEncodingException ex) {
                log.warn("字符串(" + searchTerm + ")解码错误", ex);
            }
        }
        return StringUtils.EMPTY;
    }


    /**
     * @return HttpServletRequest
     */
    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 是否是调试模式
     */
    protected boolean isDebug() {
        return ConvertUtils.toBoolean(Optional.ofNullable(httpRequest).orElse(getRequest())
                .getParameter(Constants.PARA_DEBUG), false);
    }
}
