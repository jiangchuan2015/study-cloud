package chuan.study.cloud.pojo.domain.user;

import chuan.study.cloud.common.Constants;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Data
@NoArgsConstructor
public class SerializableToken implements Serializable, Cloneable {
    private static final long serialVersionUID = 3055690144410924630L;
    private Integer userId;
    private Long timestamp;
    private Long host;
    private Integer userType;

    /**
     * 让每次生成的Token样子有很大变化
     */
    private Integer random;

    @Builder
    public SerializableToken(Integer userId, Integer userType, Long host) {
        this.timestamp = System.currentTimeMillis() - Constants.EPOCH_MILLI;
        this.random = ThreadLocalRandom.current().nextInt(100_100_100);
        this.userId = null == userId ? null : userId + random;
        this.host = null == host ? null : host + random;
        this.userType = null == userType ? null : userType + random;
    }
}
