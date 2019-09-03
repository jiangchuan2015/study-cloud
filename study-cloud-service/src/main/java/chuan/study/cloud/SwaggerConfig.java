package chuan.study.cloud;

import chuan.study.cloud.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket allApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("所有接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage(SwaggerConfig.class.getPackage().getName()))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(getGlobalHeaderParameters())
                .apiInfo(getApiInfo());
    }


    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder().title("接口文档").version("1.0.0")
                .contact(new Contact("姜川", "", "jctr@qq.com"))
                .build();
    }

    /**
     * 增加全局默认参数
     *
     * @return 参数列表
     */
    private List<Parameter> getGlobalHeaderParameters() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("token").description("授权令牌")
                .modelRef(new ModelRef("string")).parameterType("header").required(false)
                .defaultValue(RandomStringUtils.randomAlphanumeric(16)).build());

        parameters.add(new ParameterBuilder()
                .name(Constants.PARA_DEBUG).description("用作调试，如果为true则不验证令牌和权限")
                .modelRef(new ModelRef("boolean")).parameterType("query").required(false)
                .defaultValue("false").build());

        return parameters;
    }
}