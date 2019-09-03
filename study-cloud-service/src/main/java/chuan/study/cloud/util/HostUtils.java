package chuan.study.cloud.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.regex.Pattern;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
public class HostUtils {
    private static final char IP_SEPARATOR = '.';
    private static final int IP_SECTION_COUNT = 3;
    private static final String IP_REGEX = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
    private static final String[] IP_HEADER_NAMES = {"X-Real-IP", "X-Forward-For", "x-forward-for", "Proxy-Client-IP", "WL-Proxy-Client-IP"};

    /**
     * 获取本机 IP.
     *
     * @return 本机IP, 如果异常则返回 null
     */
    public static String getLocalIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (Exception ex) {
            log.error("Can not get ip of local machine", ex);
            return null;
        }
    }

    /**
     * 将IP转换成数字
     *
     * @param ip 字符串IP
     * @return 数字IP
     */
    public static Long ip2Num(String ip) {
        Long ipNum = 0L;
        if (StringUtils.isBlank(ip) || !Pattern.matches(IP_REGEX, StringUtils.trimToEmpty(ip))) {
            return ipNum;
        }

        String[] sections = StringUtils.split(ip, IP_SEPARATOR);
        for (int i = IP_SECTION_COUNT; i >= 0; i--) {
            ipNum |= Long.parseLong(sections[IP_SECTION_COUNT - i]) << (i * 8);
        }

        return ipNum;
    }

    /**
     * 将数字的IP转换成正常的字符串
     *
     * @param ipNum 数字IP
     * @return 字符串IP
     */
    public static String num2Ip(Long ipNum) {
        if (null == ipNum || ipNum <= 0) {
            return StringUtils.EMPTY;
        }

        StringBuilder result = new StringBuilder(15);
        for (int i = 0; i < IP_SECTION_COUNT + 1; i++) {
            result.insert(0, Long.toString(ipNum & 0xff)).insert(0, IP_SEPARATOR);
            ipNum = ipNum >> 8;
        }
        return result.deleteCharAt(0).toString();
    }

    /**
     * 获取客户端IP.
     *
     * @param request HTTP Request
     * @return 客户端IP
     */
    public static String getRequestIp(HttpServletRequest request) {
        String unknown = "unknown", ip = "";
        for (String header : IP_HEADER_NAMES) {
            ip = request.getHeader(header);
            if (StringUtils.isNotBlank(ip) && !StringUtils.equalsIgnoreCase(unknown, ip)) {
                log.info("根据请求头:{}, 获取到IP:{}", header, ip);
                break;
            }
        }

        if (StringUtils.isBlank(ip) || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip)) {
                ip = getLocalIp();
            }
        }

        if (StringUtils.isNotBlank(ip) && ip.length() > 15 && ip.indexOf(',') > -1) {
            ip = ip.substring(0, ip.indexOf(','));
        }

        return ip;
    }
}
