package chuan.study.cloud.task;

import chuan.study.cloud.common.Constants;
import chuan.study.cloud.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.kafka.debug", havingValue = "true")
public class KafkaReceiver {

    @KafkaListener(groupId = "MQ-RECEIVER", topics = {Constants.KAFKA_TOPIC_NAME})
    public void listen(List<ConsumerRecord<?, ?>> records) {
        if (CollectionUtils.isEmpty(records)) {
            log.warn("[KAFKA] - 无数据可处理");
        }

        records.forEach(record -> log.info("[KAFKA] - createTime:{}, partition:{}, key:{}, val:{}",
                record.timestamp(), record.partition(), record.key(), record.value()));
    }
}
