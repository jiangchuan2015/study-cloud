package chuan.study.cloud.common;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public interface Constants {
    /**
     * 系统启动时间
     */
    Date SYSTEM_START_TIME = new Date();

    /**
     * 时间戳差值的起点
     */
    long EPOCH_MILLI = LocalDate.of(2019, 1, 1)
            .atStartOfDay(ZoneOffset.ofHours(8)).toInstant()
            .toEpochMilli();

    /**
     * 系统默认编码
     */
    String DEFAULT_CHAR_SET = "UTF-8";

    /**
     * 日期格式
     */
    String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 紧凑的日期格式（主要用于生成目录名）
     */
    String COMPACT_DATE_FORMAT = "yyyyMMdd";

    /**
     * 日期时间格式
     */
    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 登陆后的标志
     */
    String TOKEN_PARA_NAME = "token";

    /**
     * 验证码 Session 中的Key
     */
    String CAPTCHA_PARA_NAME = "CAPTCHA_CODE";

    /**
     * 调试标志
     */
    String PARA_DEBUG = "debug";

    /**
     * 采用二进制的方式进行内部数据传输
     */
    String PRODUCE_BINARY_TYPE = "application/x-protostuff;charset=UTF-8";
    String CONSUME_BINARY_TYPE = "application/x-protostuff";


    /**
     * 测试 Kafka Topic
     */
    String KAFKA_TOPIC_NAME = "chuan-study";
}
