package chuan.study.cloud.web.function;

import java.util.List;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@FunctionalInterface
public interface ViewObjectConverter<M, V> {

    /**
     * 批量将 Models 转换成 VOs
     *
     * @param models 数据库中查出的对象
     * @return 用于页面显示的对象（VO）
     */
    List<V> toVos(List<M> models);
}
