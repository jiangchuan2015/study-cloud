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
public enum StateEnum implements IEnumType {

    /**
     * 无效，理论上可以从数据库中物理删除
     */
    COM_INACTIVE(0, "无效"),

    /**
     * 被禁用，可以被清理程序逐步解除引用后变成 INACTIVE
     */
    COM_DISABLED(1, "禁用"),

    /**
     * 正在初始化
     */
    COM_INITIALIZE(3, "正在初始化"),

    /**
     * 草稿
     */
    COM_DRAFT(4, "草稿"),


    /**
     * 数据正常有效
     */
    COM_ACTIVE(5, "有效"),

    /**
     * 进行中
     */
    COM_UNDERWAY(6, "进行中"),


    // ===========================>↓ ITM_XXX - 商品(10-19) ↓<===========================

    /**
     * 商品上架销售中
     */
    ITM_ONLINE(11, "销售中"),

    /**
     * 商品下架，停止销售
     */
    ITM_OFFLINE(12, "仓库中"),


    // ===========================>↓ PAY_XXX - 支付(20-29) ↓<===========================

    /**
     * 待审核
     */
    PAY_PENDING(20, "待审核"),


    // ===========================>↓ ORD_XXX - 订单(30-39) ↓<===========================

    /**
     * 已完成
     */
    ORD_COMPLETED(30, "已完成"),

    // ===========================>↓ MSG_XXX - 消息(40-49) ↓<===========================

    /**
     * 消息已读
     */
    MSG_READ(41, "已读"),

    /**
     * 消息未读
     */
    MSG_UNREAD(42, "未读"),

    // ===========================>↓ MSG_XXX - 用户审核(40-49) ↓<===========================

    USER_AUDIT_PENDING(51, "待审核"),

    USER_AUDIT_APPROVE(52, "审核通过"),

    USER_AUDIT_FAILURE(53, "审核未通过"),
    ;


    private final static Map<Integer, StateEnum> BY_CODE_MAP =
            Arrays.stream(StateEnum.values())
                    .collect(Collectors.toMap(StateEnum::getCode, type -> type));

    private final static Map<String, StateEnum> BY_NAME_MAP
            = Arrays.stream(StateEnum.values())
            .collect(Collectors.toMap(type -> type.name().toLowerCase(), type -> type));

    @EnumValue
    private final int code;
    private final String desc;


    /**
     * 获取枚举对象
     *
     * @param code 代码
     * @return 根据编码转换出来的枚举对象
     */
    public static StateEnum parse(Integer code) {
        return BY_CODE_MAP.get(code);
    }

    /**
     * 获取枚举对象
     *
     * @param name 名字
     * @return 根据枚举名转换出来的枚举对象
     */
    public static StateEnum parse(String name) {
        return BY_NAME_MAP.get(StringUtils.trimToEmpty(name).toLowerCase());
    }
}