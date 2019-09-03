package chuan.study.cloud.config;

import chuan.study.cloud.web.converter.CustomHttpMessageConverter;
import chuan.study.cloud.web.json.JsonResultHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@EnableAutoConfiguration
public class CustomConfiguration implements WebMvcConfigurer {
    /**
     * 开发环境
     */
    private static final String ENV_DEV = "dev";

    /**
     * 获取当前环境
     */
    @Value("${spring.profiles:dev}")
    private String springProfiles;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (StringUtils.containsIgnoreCase(springProfiles, ENV_DEV)) {
            registry.addMapping("/**")
                    .allowedMethods(
                            RequestMethod.GET.name(),
                            RequestMethod.POST.name(),
                            RequestMethod.PUT.name(),
                            RequestMethod.DELETE.name(),
                            RequestMethod.OPTIONS.name()
                    ).allowedOrigins("*");
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 增加拦截器
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        returnValueHandlers.add(jsonResultHandler());
    }

    /**
     * 对JSON结果进行处理
     */
    @Bean
    public JsonResultHandler jsonResultHandler() {
        return new JsonResultHandler();
    }

    /**
     * 内部服务调用采用二进制方式传输数据
     */
    @Bean
    public CustomHttpMessageConverter customHttpMessageConverter() {
        return new CustomHttpMessageConverter();
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);
        return factory;
    }
}
