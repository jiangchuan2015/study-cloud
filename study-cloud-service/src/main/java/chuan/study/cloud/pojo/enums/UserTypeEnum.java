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
public enum UserTypeEnum implements IEnumType {
    /**
     * 运营中心
     */
    OPERATION_CENTER(9, "运营中心"),

    /**
     * 区域代理商
     */
    REGIONAL_AGENT(7, "区域代理商"),

    /**
     * 城市代理商
     */
    CITY_AGENT(5, "城市代理商"),

    /**
     * 创客
     */
    MAKER(3, "创客"),

    /**
     * 会员
     */
    MEMBER(1, "会员");


    private final static Map<Integer, UserTypeEnum> BY_CODE_MAP =
            Arrays.stream(UserTypeEnum.values()).collect(Collectors.toMap(UserTypeEnum::getCode, type -> type));

    private final static Map<String, UserTypeEnum> BY_NAME_MAP
            = Arrays.stream(UserTypeEnum.values()).collect(Collectors.toMap(type -> type.name().toLowerCase(), type -> type));

    @EnumValue
    private final int code;
    private final String desc;


    /**
     * 将代码转成枚举
     *
     * @param code 代码
     * @return 转换出来的客户端类型
     */
    public static UserTypeEnum parse(Integer code) {
        if (null == code) {
            return null;
        }
        return BY_CODE_MAP.get(code);
    }

    /**
     * @param name 名字
     * @return 转换出来的客户端类型
     */
    public static UserTypeEnum parse(String name) {
        return BY_NAME_MAP.get(StringUtils.trimToEmpty(name).toLowerCase());
    }
}
