package chuan.study.cloud.pojo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Getter
@AllArgsConstructor
public enum GenderEnum implements IEnumType {
    /**
     * 未知
     */
    UNKNOWN(0, "未知"),

    /**
     * 男
     */
    MALE(1, "男"),

    /**
     * 女
     */
    FEMALE(2, "女");


    private final static Map<Integer, GenderEnum> BY_CODE_MAP =
            Arrays.stream(GenderEnum.values()).collect(Collectors.toMap(GenderEnum::getCode, type -> type));

    private final static Map<String, GenderEnum> BY_NAME_MAP =
            Arrays.stream(GenderEnum.values()).collect(Collectors.toMap(type -> type.name().toLowerCase(), type -> type));

    @EnumValue
    private final int code;
    private final String desc;


    /**
     * @param code 代码
     * @return 转换出来的性别
     */
    public static GenderEnum parse(Integer code) {
        return BY_CODE_MAP.getOrDefault(code, GenderEnum.UNKNOWN);
    }

    /**
     * @param name 名字
     * @return 转换出来的性别
     */
    public static GenderEnum parse(String name) {
        return BY_NAME_MAP.getOrDefault(StringUtils.trimToEmpty(name).toLowerCase(), GenderEnum.UNKNOWN);
    }
}