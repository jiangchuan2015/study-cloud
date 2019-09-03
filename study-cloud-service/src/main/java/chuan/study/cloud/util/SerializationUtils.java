package chuan.study.cloud.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
public class SerializationUtils {
    /**
     * 需要使用包装类进行序列化/反序列化的class集合
     */
    private static final Set<Class<?>> WRAPPER_CLASSES = Sets.newConcurrentHashSet();

    /**
     * 序列化/反序列化包装类 Class 对象
     */
    private static final Class<SerializerWrapper> WRAPPER_CLASS = SerializerWrapper.class;

    /**
     * 序列化/反序列化包装类 Schema 对象
     */
    private static final Schema<SerializerWrapper> WRAPPER_SCHEMA = RuntimeSchema.createFrom(WRAPPER_CLASS);

    /**
     * 缓存对象及对象schema信息集合
     */
    private static final Map<Class<?>, Schema<?>> CACHED_SCHEMA_MAP = Maps.newConcurrentMap();

    /*
     * 预定义一些ProtoStuff无法直接序列化/反序列化的对象
     */
    static {
        WRAPPER_CLASSES.add(boolean.class);
        WRAPPER_CLASSES.add(Boolean.class);
        WRAPPER_CLASSES.add(byte.class);
        WRAPPER_CLASSES.add(Byte.class);
        WRAPPER_CLASSES.add(char.class);
        WRAPPER_CLASSES.add(Character.class);
        WRAPPER_CLASSES.add(short.class);
        WRAPPER_CLASSES.add(Short.class);
        WRAPPER_CLASSES.add(int.class);
        WRAPPER_CLASSES.add(Integer.class);
        WRAPPER_CLASSES.add(float.class);
        WRAPPER_CLASSES.add(Float.class);
        WRAPPER_CLASSES.add(long.class);
        WRAPPER_CLASSES.add(Long.class);
        WRAPPER_CLASSES.add(double.class);
        WRAPPER_CLASSES.add(Double.class);

        WRAPPER_CLASSES.add(List.class);
        WRAPPER_CLASSES.add(ArrayList.class);
        WRAPPER_CLASSES.add(CopyOnWriteArrayList.class);
        WRAPPER_CLASSES.add(LinkedList.class);
        WRAPPER_CLASSES.add(Stack.class);
        WRAPPER_CLASSES.add(Vector.class);

        WRAPPER_CLASSES.add(Map.class);
        WRAPPER_CLASSES.add(HashMap.class);
        WRAPPER_CLASSES.add(TreeMap.class);
        WRAPPER_CLASSES.add(Hashtable.class);
        WRAPPER_CLASSES.add(SortedMap.class);

        WRAPPER_CLASSES.add(Object.class);
    }

    /**
     * 注册需要使用包装类进行序列化/反序列化的 Class 对象
     *
     * @param clazz 需要包装的类型 Class 对象
     */
    public static void registerWrapperClass(Class clazz) {
        WRAPPER_CLASSES.add(clazz);
    }


    /**
     * 序列化对象
     *
     * @param obj 需要序列化的对象
     * @param <T> 序列化对象的类型
     * @return 序列化后的二进制数组
     */
    public static <T> byte[] serialize(T obj) {
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Class<T> clazz = (Class<T>) obj.getClass();
            Object serializeObject = obj;
            Schema schema = WRAPPER_SCHEMA;
            if (WRAPPER_CLASSES.contains(clazz)) {
                serializeObject = SerializerWrapper.builder(obj);
            } else {
                schema = getSchema(clazz);
            }
            return ProtostuffIOUtil.toByteArray(serializeObject, schema, linkedBuffer);
        } catch (Exception ex) {
            log.error("序列化对象异常 [" + obj + "]", ex);
            throw new IllegalStateException(ex.getMessage(), ex);
        } finally {
            linkedBuffer.clear();
        }
    }

    /**
     * 反序列化对象
     *
     * @param data  需要反序列化的二进制数组
     * @param clazz 反序列化后的对象class
     * @param <T>   反序列化后的对象类型
     * @return 反序列化后的对象集合
     */
    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            if (WRAPPER_CLASSES.contains(clazz)) {
                SerializerWrapper<T> wrapper = new SerializerWrapper<>();
                ProtostuffIOUtil.mergeFrom(data, wrapper, WRAPPER_SCHEMA);
                return wrapper.getData();
            } else {
                T message = clazz.newInstance();
                ProtostuffIOUtil.mergeFrom(data, message, getSchema(clazz));
                return message;
            }
        } catch (Exception ex) {
            log.error("反序列化对象异常 [" + clazz.getName() + "]", ex);
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    /**
     * 反序列化对象
     *
     * @param data  需要反序列化的流
     * @param clazz 反序列化后的对象class
     * @param <T>   反序列化后的对象类型
     * @return 反序列化后的对象集合
     */
    public static <T> T deserialize(InputStream data, Class<T> clazz) {
        try {
            if (WRAPPER_CLASSES.contains(clazz)) {
                SerializerWrapper<T> wrapper = new SerializerWrapper<>();
                ProtostuffIOUtil.mergeFrom(data, wrapper, WRAPPER_SCHEMA);
                return wrapper.getData();
            } else {
                T message = clazz.newInstance();
                ProtostuffIOUtil.mergeFrom(data, message, getSchema(clazz));
                return message;
            }
        } catch (Exception ex) {
            log.error("反序列化对象异常 [" + clazz.getName() + "]", ex);
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }


    /**
     * 获取序列化对象类型的Schema
     *
     * @param clazz 序列化对象的class
     * @param <T>   序列化对象的类型
     * @return 序列化对象类型的Schema
     */
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) CACHED_SCHEMA_MAP.get(clazz);
        if (null == schema) {
            schema = RuntimeSchema.createFrom(clazz);
            CACHED_SCHEMA_MAP.put(clazz, schema);
        }
        return schema;
    }


    @Data
    static class SerializerWrapper<T> {
        private T data;

        public static <T> SerializerWrapper<T> builder(T data) {
            SerializerWrapper<T> wrapper = new SerializerWrapper<>();
            wrapper.setData(data);
            return wrapper;
        }
    }
}
