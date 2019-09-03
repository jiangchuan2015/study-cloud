package chuan.study.cloud.pojo.enums;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public interface IEnumType {
    /**
     * 数据库中定义的 数字 状态码
     *
     * @return 数字状态
     */
    int getCode();

    /**
     * 简单描述
     *
     * @return 简单描述
     */
    String getDesc();
}
