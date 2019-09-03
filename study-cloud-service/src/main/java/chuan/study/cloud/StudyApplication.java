package chuan.study.cloud;

import chuan.study.cloud.config.CustomConfiguration;
import chuan.study.cloud.starter.qqwry.EnableQQwry;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
@EnableAsync
@EnableQQwry
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("chuan.study.**.repository")
public class StudyApplication extends CustomConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(StudyApplication.class, args);
    }
}
