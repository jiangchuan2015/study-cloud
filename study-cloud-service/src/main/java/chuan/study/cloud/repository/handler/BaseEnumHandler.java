package chuan.study.cloud.repository.handler;

import chuan.study.cloud.pojo.enums.IEnumType;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public abstract class BaseEnumHandler<T extends IEnumType> extends BaseObjectHandler<T> {
    /**
     * 通过数字(存储在DB中)获取得枚举
     *
     * @param code 代码
     * @return
     */
    public abstract T getEnumeration(int code);

    @Override
    public int getCode(T enumeration) {
        return enumeration.getCode();
    }

    @Override
    public T getObject(int code) {
        return getEnumeration(code);
    }
}
