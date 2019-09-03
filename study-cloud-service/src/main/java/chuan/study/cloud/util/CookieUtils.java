package chuan.study.cloud.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
public final class CookieUtils {
    private static int DEFAULT_MAX_AGE = (int) TimeUnit.HOURS.toSeconds(1);

    /**
     * 根据 cookie 名字获取 cookie
     *
     * @param request Http Request
     * @param name    Cookie 名称
     * @return Cookie
     */
    public static Cookie get(HttpServletRequest request, String name) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(name, "Cookie 名字不能为空");
        Cookie cookies[] = request.getCookies();
        if (ArrayUtils.isEmpty(cookies)) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * 增加 cookie
     *
     * @param request  Http Request
     * @param response Http Response
     * @param name     要增加的 Cookie 名称
     * @param value    要增加的 Cookie 值
     */
    public static void add(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        add(request, response, name, value, null, null, null, true, false);
    }

    /**
     * 增加 cookie
     *
     * @param request  Http Request
     * @param response Http Response
     * @param name     要增加的 Cookie 名称
     * @param value    要增加的 Cookie 值
     * @param maxAge   要增加的 Cookie 的最长有效期
     */
    public static void add(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) {
        add(request, response, name, value, maxAge, null, null, true, false);
    }

    /**
     * 增加 cookie
     *
     * @param request  Http Request
     * @param response Http Response
     * @param name     要增加的 Cookie 名称
     * @param value    要增加的 Cookie 值
     * @param maxAge   要增加的 Cookie 的最长有效期
     * @param path     要增加的 Cookie 有效域名下的路径
     */
    public static void add(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge, String path) {
        add(request, response, name, value, maxAge, path, null, true, false);
    }

    /**
     * 增加 cookie
     *
     * @param request  Http Request
     * @param response Http Response
     * @param name     要增加的 Cookie 名称
     * @param value    要增加的 Cookie 值
     * @param maxAge   要增加的 Cookie 的最长有效期
     * @param path     要增加的 Cookie 有效域名下的路径
     * @param domain   要增加的 Cookie 有效作用域名
     * @param httpOnly httpOnly
     * @param secured  secured 只有 https 可见
     */
    public static void add(HttpServletRequest request, HttpServletResponse response,
                           String name, String value, Integer maxAge, String path, String domain,
                           boolean httpOnly, boolean secured) {

        Objects.requireNonNull(request);
        Objects.requireNonNull(response);
        Objects.requireNonNull(name, "Cookie 名字不能为空");

        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(null == maxAge ? DEFAULT_MAX_AGE : maxAge);

        // 设置域名
        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain.trim());
        }

        // 设置路径
        if (StringUtils.isBlank(path)) {
            path = StringUtils.isBlank(request.getContextPath()) ? "/" : request.getContextPath();
        }
        cookie.setPath(path.trim());

        // 设置安全项， 只有 https 可见
        if (secured) {
            cookie.setSecure(true);
        }

        // 是否允许 JS 访问
        if (httpOnly) {
            cookie.setHttpOnly(true);
        }
        response.addCookie(cookie);
    }


    /**
     * 删除 Cookie
     *
     * @param request  Http Request
     * @param response Http Response
     * @param name     要删除的 Cookie 名称
     */
    public static void delete(HttpServletRequest request, HttpServletResponse response, String name) {
        delete(request, response, name, null);
    }

    /**
     * 删除 Cookie
     *
     * @param request  Http Request
     * @param response Http Response
     * @param name     要删除的 Cookie 名称
     * @param path     要删除的 Cookie 有效域名下的路径
     */
    public static void delete(HttpServletRequest request, HttpServletResponse response, String name, String path) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);
        Objects.requireNonNull(name, "Cookie 名字不能为空");

        Cookie cookie = get(request, name);
        if (null != cookie) {
            if (StringUtils.isBlank(path)) {
                path = StringUtils.isBlank(request.getContextPath()) ? "/" : request.getContextPath();
            }

            cookie.setValue("");
            cookie.setMaxAge(0);
            cookie.setPath(path);
            response.addCookie(cookie);
        }
    }
}
