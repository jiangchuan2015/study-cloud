package chuan.study.cloud.repository.handler;

import chuan.study.cloud.pojo.enums.StateEnum;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public class EnumStateHandler extends BaseEnumHandler<StateEnum> {
    @Override
    public StateEnum getEnumeration(int code) {
        return StateEnum.parse(code);
    }
}
