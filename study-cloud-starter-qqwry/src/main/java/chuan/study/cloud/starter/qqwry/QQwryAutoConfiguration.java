package chuan.study.cloud.starter.qqwry;

import com.github.jarod.qqwry.QQWry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-09-03
 */
@Slf4j
@Configuration
public class QQwryAutoConfiguration implements ResourceLoaderAware {
    
    private ResourceLoader resourceLoader;

    /**
     * 获取QQwry的数据文件地址
     */
    @Value("${qqwry.location}")
    private String location;

    @Bean
    public QQWry qqwry() throws IOException {
        // 如果没有配置，则从 classpath 下加载
        if (StringUtils.isEmpty(location)) {
            return new QQWry();
        }

        // 根据配置的文件路径加载
        if (new File(location).exists()) {
            return new QQWry(Paths.get(location));
        }

        // 查找resource
        Resource resource = resourceLoader.getResource(location);
        try {
            if (resource.exists()) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                FileCopyUtils.copy(resource.getInputStream(), output);
                return new QQWry(output.toByteArray());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return new QQWry();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
