package chuan.study.cloud.util;

import chuan.study.cloud.exception.BusinessException;
import chuan.study.cloud.pojo.enums.ResponseCodeEnum;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public final class CacheKeys {
    private static final String SEPARATOR = ":";

    /**
     * 根据参数生成缓存Key
     *
     * @param prefix 前缀
     * @param keys   用作唯一标识缓存Key
     * @return 生成出来的缓存Key
     */
    public static String getKey(String prefix, String... keys) {
        if (ArrayUtils.isEmpty(keys)) {
            throw new BusinessException(ResponseCodeEnum.PARAMETER_REQUIRED);
        }

        // 只有一个
        if (1 == keys.length && StringUtils.isNotBlank(keys[0])) {
            return (prefix.trim() + SEPARATOR + keys[0].trim()).toLowerCase();
        }

        // 将多个字符连接起来
        String keyStr = Arrays.stream(keys).map(StringUtils::trimToNull)
                .filter(Objects::nonNull).sorted().collect(Collectors.joining("_"));
        if (StringUtils.isBlank(keyStr)) {
            throw new BusinessException(ResponseCodeEnum.PARAMETER_REQUIRED);
        }

        return (prefix.trim() + SEPARATOR + keyStr).toLowerCase();
    }


    /**
     * 根据参数生成缓存Key
     * CacheKeys.getKey(CacheGroupEnum.ORDER, 100) => ord:100
     *
     * @param prefix 前缀
     * @param keys   用作唯一标识缓存Key
     * @return 生成出来的缓存Key
     */
    public static String getKey(String prefix, Number... keys) {
        if (ArrayUtils.isEmpty(keys)) {
            throw new BusinessException(ResponseCodeEnum.PARAMETER_REQUIRED);
        }

        // 只有一个
        if (1 == keys.length && NumberUtils.isPositive(keys[0])) {
            return (prefix.trim() + SEPARATOR + keys[0]).toLowerCase();
        }

        // 将多个字符连接起来
        String keyStr = Arrays.stream(keys).filter(NumberUtils::isPositive).sorted()
                .map(String::valueOf).collect(Collectors.joining("_"));
        if (StringUtils.isBlank(keyStr)) {
            throw new BusinessException(ResponseCodeEnum.PARAMETER_REQUIRED);
        }

        return (prefix.trim() + SEPARATOR + keyStr).toLowerCase();
    }


    /**
     * 图片验证码缓存Key
     *
     * @param hashing 验证码哈希值
     * @return 生成出来的Key
     */
    public static String getCaptchaImageKey(String hashing) {
        return getKey("captcha" + SEPARATOR + "img", hashing);
    }

    /**
     * 根据用户ID生成缓存Key
     *
     * @param userId 用户ID
     * @return 缓存Key, eg: user:sso:context::123
     */
    public static String getUserContextKey(int userId) {
        return getKey(String.join(SEPARATOR, "user", "sso", "context"), userId);
    }

    /**
     * 生成微信AccessToken的缓存Key
     *
     * @return 缓存Key, eg: wechat:access:token
     */
    public static String getWechatAccessTokenKey() {
        return String.join(SEPARATOR, "wechat", "access", "token");
    }


    /**
     * 生成短信验证码
     *
     * @param mobile 手机号
     * @return 缓存Key, eg: msg:sms:captcha::13912345678
     */
    public static String getShortMessageCaptchaKey(String mobile) {
        return getKey(String.join(SEPARATOR, "msg", "sms", "captcha"), mobile.trim());
    }
}
