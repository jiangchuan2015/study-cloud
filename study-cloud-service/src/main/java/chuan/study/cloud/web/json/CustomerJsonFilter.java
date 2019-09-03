package chuan.study.cloud.web.json;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@JsonFilter("CustomerJsonFilter")
public class CustomerJsonFilter extends FilterProvider {
    /**
     * 需要保留的字段
     */
    private Map<Class<?>, Set<String>> includeMap = new HashMap<>();

    /**
     * 需要排除的字段
     */
    private Map<Class<?>, Set<String>> excludeMap = new HashMap<>();

    /**
     * 收集需要保留的字段
     *
     * @param clz    数据类型
     * @param fields 保留的字段
     */
    public void include(Class<?> clz, String[] fields) {
        Set<String> existedFields = includeMap.getOrDefault(clz, new HashSet<>());
        existedFields.addAll(Arrays.asList(fields));
        includeMap.put(clz, existedFields);
    }

    /**
     * 收集需要排除的字段
     *
     * @param clz    数据类型
     * @param fields 排除的字段
     */
    public void exclude(Class<?> clz, String[] fields) {
        Set<String> existedFields = excludeMap.getOrDefault(clz, new HashSet<>());
        existedFields.addAll(Arrays.asList(fields));
        excludeMap.put(clz, existedFields);
    }

    @Override
    public BeanPropertyFilter findFilter(Object filterId) {
        throw new UnsupportedOperationException("Access to deprecated filters not supported");
    }

    @Override
    public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
        return new SimpleBeanPropertyFilter() {
            @Override
            public void serializeAsField(Object pojo, JsonGenerator generator, SerializerProvider prov, PropertyWriter writer) throws Exception {
                if (apply(pojo.getClass(), writer.getName())) {
                    writer.serializeAsField(pojo, generator, prov);
                } else if (!generator.canOmitFields()) {
                    writer.serializeAsOmittedField(pojo, generator, prov);
                }
            }
        };
    }

    /**
     * 检查字段是否需要保留
     *
     * @param type 数据类型
     * @param name 字段名称
     * @return 是否需要保留
     */
    private boolean apply(Class<?> type, String name) {
        Set<String> includes = includeMap.get(type);
        Set<String> excludes = excludeMap.get(type);
        if (includes != null && includes.contains(name)) {
            return true;
        } else if (excludes != null && !excludes.contains(name)) {
            return true;
        } else if (includes == null && excludes == null) {
            return true;
        }
        return false;
    }
}
