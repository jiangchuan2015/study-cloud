package chuan.study.cloud.web.converter;

import chuan.study.cloud.util.SerializationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
public class CustomHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final MediaType PRO_TO_STUFF = new MediaType("application", "x-protostuff", DEFAULT_CHARSET);

    public CustomHttpMessageConverter() {
        super(PRO_TO_STUFF);
    }

    @Override
    protected boolean canRead(MediaType mediaType) {
        return super.canRead(mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return super.canWrite(clazz, mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(PRO_TO_STUFF);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        MediaType contentType = Optional.ofNullable(inputMessage.getHeaders().getContentType()).orElse(PRO_TO_STUFF);
        if (!PRO_TO_STUFF.isCompatibleWith(contentType)) {
            log.error("不支持的解码格式({}), 请使用({})作为ContentType", contentType.getSubtype(), PRO_TO_STUFF.getSubtype());
        }

        try {
            return SerializationUtils.deserialize(inputMessage.getBody(), clazz);
        } catch (Exception ex) {
            throw new HttpMessageNotReadableException("Could not read protostuff message", inputMessage);
        }
    }

    @Override
    public Object read(Type type, Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        if (type instanceof ParameterizedType) {
            return readInternal((Class) ((ParameterizedType) type).getRawType(), inputMessage);
        } else if (type instanceof Class) {
            return readInternal((Class) type, inputMessage);
        }
        return null;
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = Optional.ofNullable(outputMessage.getHeaders().getContentType()).orElse(PRO_TO_STUFF);
        if (!PRO_TO_STUFF.isCompatibleWith(contentType)) {
            log.error("不支持的解码格式({}), 请使用({})作为ContentType", contentType.getSubtype(), PRO_TO_STUFF.getSubtype());
        }

        byte[] serialize = SerializationUtils.serialize(object);
        if (log.isDebugEnabled()) {
            // 使二进制数据可视化，便于测试
            log.debug("序列化\n前({}), \n后[{}]", object, new String(Base64.getEncoder().encode(serialize)));
        }

        FileCopyUtils.copy(serialize, outputMessage.getBody());
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        writeInternal(object, outputMessage);
    }
}
