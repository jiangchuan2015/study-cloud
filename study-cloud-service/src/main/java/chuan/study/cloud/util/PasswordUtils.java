package chuan.study.cloud.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public final class PasswordUtils {
    /**
     * 将密码转成 md5, 用 UserID 加盐
     *
     * @param userId 用户ID
     * @param plain  密码明文
     * @return md5后的密码
     */
    public static String generatePassword(int userId, String plain) {
        if (StringUtils.isBlank(plain)) {
            plain = RandomStringUtils.randomAlphanumeric(8);
        }

        // 将密码转成 md5, 用 UserID 加盐
        return DigestUtils.md5Hex(plain.trim() + '|' + StringUtils.leftPad(Integer.toUnsignedString(userId, 32), 6, '0'));
    }

    /**
     * TODO 检查密码是否相同
     *
     * @param userId     用户ID
     * @param plain      密码明文
     * @param encryption 已经加密后的密码
     * @return true: 两密码相同，false:密码不同
     */
    public static boolean checkPassword(int userId, String plain, String encryption) {
        return generatePassword(userId, plain).equals(encryption);
    }
}
