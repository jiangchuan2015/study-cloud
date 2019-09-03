package chuan.study.cloud.task;

import chuan.study.cloud.common.Constants;
import chuan.study.cloud.util.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.kafka.debug", havingValue = "true")
public class KafkaSender {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final ScheduledExecutorService THREAD_POOL = new ScheduledThreadPoolExecutor(10,
            new BasicThreadFactory.Builder().namingPattern("MQ-SENDER-%d").daemon(true).build());

    public KafkaSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

        THREAD_POOL.scheduleAtFixedRate(() -> {
            IntStream.iterate(0, i -> i + 1).limit(10).forEach(idx -> {
                String message = RandomStringUtils.randomAlphabetic(32);
                log.info("开始发送消息: {}", message);
                this.kafkaTemplate.send(Constants.KAFKA_TOPIC_NAME, String.valueOf(SystemClock.now()), message);
            });
        }, 0, 30, TimeUnit.SECONDS);

    }

}
