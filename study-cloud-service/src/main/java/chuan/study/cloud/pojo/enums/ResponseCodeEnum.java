package chuan.study.cloud.pojo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Getter
public enum ResponseCodeEnum implements IEnumType {

    /**
     * 成功
     */
    SUCCESS(200, "成功"),

    // 全局错误
    SYSTEM_ERROR(99_010, "系统异常"),
    BUSINESS_ERROR(99_020, "业务异常", "(%s)业务发生异常"),
    BUSINESS_ERROR_DEFINE(99_021, "业务异常", "%s"),
    UNKNOWN_ERROR(99_030, "未知错误"),

    // 用户认证
    UNAUTHORIZED(98_010, "用户未授权", "用户[%s]未授权"),
    LOGIN_FAILED(98_020, "登录失败", "%s"),
    TOKEN_EXPIRED(98_030, "授权令牌已失效", "授权令牌[%s]已失效"),
    PERMISSION_DENIED(98_040, "无权查询数据", "[%s]无权查询数据"),
    USER_NOT_REGISTER(98_050, "用户未注册", "用户[%s]未注册"),

    // 参数校验
    PARAMETER_REQUIRED(97_010, "参数必传", "参数[%s]不能不空"),
    PARAMETER_FORMAT_ERROR(97_020, "参数格式错误", "参数[%s]格式不正确"),
    PARAMETER_RANGE_ERROR(97_030, "数据范围错误"),
    PARAMETER_VALUE_ERROR(97_040, "参数值错误", "(%s)"),
    PARAMETER_FILE_NULL(97_050, "文件错误", "(%s)"),


    // 增删改查
    CREATE_FAILED(96_010, "增加失败", "[%s]失败"),
    UPDATE_FAILED(96_020, "修改失败", "修改[%s]失败"),
    DELETE_FAILED(96_030, "删除失败", "删除[%s]失败"),
    DISABLE_FAILED(96_040, "禁用失败", "禁用[%s]失败"),
    ENABLE_FAILED(96_050, "启用失败", "启用[%s]失败"),
    OFFLINE_FAILED(96_060, "下线失败", "下线[%s]失败"),
    ONLINE_FAILED(96_070, "上线失败", "上线[%s]失败"),
    AUDIT_FAILED(96_080, "审核失败", "审核[%s]失败"),
    REARRANGE_FAILED(96_090, "重新排序失败", "重新排序[%s]失败"),

    // 数据操作
    SQL_EXECUTION_FAILED(95_010, "执行SQL错误"),
    DATA_NOT_FOUND(95_050, "数据未找到", "[%s]数据未找到"),
    DATA_EXISTED(95_060, "数据已经存在", "[%s]已经存在"),
    DATA_CAN_NOT_ALL_DELETED(95_080, "数据不能全部删除", "数据[%s]不能全部删除"),
    ;

    private final static Map<Integer, ResponseCodeEnum> BY_CODE_MAP = Arrays.stream(ResponseCodeEnum.values())
            .collect(Collectors.toMap(ResponseCodeEnum::getCode, code -> code));

    @EnumValue
    private final int code;
    private final String desc;
    private final String template;


    ResponseCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
        this.template = "";
    }

    ResponseCodeEnum(int code, String desc, String template) {
        this.code = code;
        this.desc = desc;
        this.template = template;
    }


    /**
     * @param code 代码
     * @return 转换出来的状态码
     */
    public static ResponseCodeEnum parse(Integer code) {
        return BY_CODE_MAP.getOrDefault(code, ResponseCodeEnum.SYSTEM_ERROR);
    }
}
