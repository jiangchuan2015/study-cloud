package chuan.study.cloud.util;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.util.stream.IntStream;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * 下划线字符
     */
    private static final char UNDER_LINE = '_';

    /**
     * 将带有下划线的字符串转换成驼峰字符串
     * download_count => downloadCount
     *
     * @param str 需要转换的字符串
     * @return 变成驼峰形式字符串
     */
    public static String underlineToCamel(String str) {
        if (isEmpty(str)) {
            return EMPTY;
        }
        String temp = str.toLowerCase();
        int len = temp.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = temp.charAt(i);
            if (c == UNDER_LINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(temp.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 将驼峰形式字符串转换成下划线形式
     * downloadCount => download_count
     *
     * @param str 需要转换的字符串
     * @return 变成下划线形式字符串
     */
    public static String camelToUnderline(String str) {
        if (isEmpty(str)) {
            return EMPTY;
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(UNDER_LINE);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 取汉语拼音的首字母
     *
     * @param chinese 汉字
     * @return 首字母
     */
    public static String getPinyinInitial(String chinese) {
        String pinyin = chineseToPinyin(chinese, true);
        return isBlank(pinyin) ? EMPTY : pinyin.substring(0, 1).toUpperCase();
    }

    /**
     * 汉字转换位汉语拼音，英文字符不变
     *
     * @param chinese       汉字
     * @param firstCharOnly 只取首字母
     * @return 拼音
     */
    public static String chineseToPinyin(String chinese, boolean firstCharOnly) {
        if (StringUtils.isBlank(chinese)) {
            return StringUtils.EMPTY;
        }

        char[] chars = chinese.toCharArray();
        HanyuPinyinOutputFormat formatter = new HanyuPinyinOutputFormat();
        formatter.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        formatter.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder pinyinBuilder = new StringBuilder();
        IntStream.range(0, chars.length).forEach(idx -> {
            if (chars[idx] > 128) {
                try {
                    String pinyin = PinyinHelper.toHanyuPinyinStringArray(chars[idx], formatter)[0];
                    pinyinBuilder.append(firstCharOnly ? pinyin.charAt(0) : pinyin);
                } catch (Exception ex) {
                    log.warn(ex.getMessage());
                }
            } else {
                pinyinBuilder.append(chars[idx]);
            }
        });
        return pinyinBuilder.toString();
    }

    /**
     * 每两个字符互换位置
     *
     * @param input 需要处理的字符串
     * @return 互换位置后的字符串
     */
    public static String swapChar(String input) {
        if (input == null) {
            return null;
        }
        char[] original = input.toCharArray();
        char[] changed = new char[original.length];
        for (int i = 0; i < original.length / 2; i++) {
            int start = i * 2;
            int next = i * 2 + 1;
            changed[start] = original[next];
            changed[next] = original[start];
        }
        if (original.length % 2 == 1) {
            changed[original.length - 1] = original[original.length - 1];
        }

        return new String(changed);
    }




    /**
     * 获取两字符串的相似度
     * 参考：https://blog.csdn.net/JavaReact/article/details/82144732
     */
    public static float getSimilarityRatio(String str, String target) {
        return 1 - (float) compareString(str, target) / Math.max(str.length(), target.length());
    }

    /**
     * 比较两个字符串的相似度
     * 核心算法：用一个二维数组记录每个字符串是否相同，如果相同记为0，不相同记为1，每行每列相同个数累加
     * 则数组最后一个数为不相同的总数，从而判断这两个字符的相识度
     *
     * @param str    源字符
     * @param target 目标字条
     * @return
     */
    private static int compareString(String str, String target) {
        int d[][];
        int n = str.length(), m = target.length();
        int i, j, temp;
        char ch1, ch2;
        if (n == 0) {
            return m;
        }

        if (m == 0) {
            return n;
        }

        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) {
            ch1 = str.charAt(i - 1);
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    /**
     * 获取最小的值
     */
    private static int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }

}
