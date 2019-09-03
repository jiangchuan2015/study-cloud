package chuan.study.cloud.util;

import chuan.study.cloud.pojo.domain.user.SerializableToken;
import chuan.study.cloud.pojo.domain.user.UserContext;
import chuan.study.cloud.pojo.enums.UserTypeEnum;
import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
public class UserTokenUtils {
    private final StringRedisTemplate redisTemplate;
    private final BlockingQueue<String> tokenQueue;
    private final Cache<Integer, UserContext> userContextCache;
    private final Cache<String, Integer> tokenCache;

    public UserTokenUtils(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.tokenQueue = new ArrayBlockingQueue<>(100_000);
        this.userContextCache = Caffeine.newBuilder()
                .initialCapacity(100).maximumSize(5_000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();

        this.tokenCache = Caffeine.newBuilder()
                .initialCapacity(100).maximumSize(5_000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();

        // 缓存池
        ScheduledExecutorService threadPool = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("TOKEN-VERIFY-%d").daemon(true).build());

        // 定时检查授权令牌的有效期
        threadPool.scheduleAtFixedRate(() -> {
            try {
                tokenVerify();
            } catch (Exception ex) {
                log.error("检查授权令牌失败", ex);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * 将授权令牌放入队列
     *
     * @param token 授权令牌
     */
    public void offer(String token) {
        if (StringUtils.isBlank(token)) {
            return;
        }

        try {
            this.tokenQueue.offer(token, 30, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("将授权令牌({})放入队列失败。", token);
        }
    }

    /**
     * 根据用户信息生成授权令牌
     *
     * @param userId 用户ID
     * @param host   登录时的主机IP
     * @return 可传递的授权令牌
     */
    public String generateToken(Integer userId, UserTypeEnum userType, String host) {
        if (null == userId || userId <= 0) {
            log.warn("用户ID({})不正确。", userId);
            return StringUtils.EMPTY;
        }

        if (StringUtils.isBlank(host)) {
            log.warn("环境信息(host={})不正确。", host);
            return StringUtils.EMPTY;
        }

        // 生成授权令牌
        SerializableToken serializableToken = SerializableToken.builder()
                .userId(userId).userType(userType.getCode()).host(HostUtils.ip2Num(host)).build();

        // 转换成可以 Header 中传递的字符串, 并加入到本地缓存
        String base64Token = Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(serializableToken));
        String finalToken = getRedundancyCode(base64Token) + StringUtils.swapChar(base64Token);
        this.tokenCache.put(finalToken, userId);

        return finalToken;
    }


    /**
     * 根据Token的原始信息解码
     *
     * @param token 授权令牌
     * @return SerializableToken
     */
    public SerializableToken getSerializableToken(String token) {
        return getSerializableToken(token, true);
    }

    /**
     * 根据Token的原始信息解码
     *
     * @param token       授权令牌
     * @param sendToQueue 是否放到队列中继续检查
     * @return SerializableToken
     */
    public SerializableToken getSerializableToken(String token, boolean sendToQueue) {
        if (sendToQueue) {
            this.offer(token);
        }
        return parseToken(token);
    }

    /**
     * 根据Token的原始信息获取用户ID
     *
     * @param token 授权令牌
     * @return 用户ID
     */
    public Integer getUserIdDirectly(String token) {
        return getUserIdDirectly(token, true);
    }

    /**
     * 根据Token的原始信息获取用户ID
     *
     * @param token       授权令牌
     * @param sendToQueue 是否放到队列中继续检查
     * @return 用户ID
     */
    public Integer getUserIdDirectly(String token, boolean sendToQueue) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        Integer userId = this.tokenCache.getIfPresent(token);
        if (NumberUtils.isPositive(userId)) {
            return userId;
        }

        return Optional.ofNullable(getSerializableToken(token, sendToQueue))
                .map(SerializableToken::getUserId)
                .orElse(null);
    }

    /**
     * 根据授权令牌获取用户信息
     *
     * @param token 授权令牌
     * @return UserContext
     */
    public UserContext getUserContext(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        // 放入队列检查
        this.offer(token);

        Integer userId = getUserIdDirectly(token, false);
        if (NumberUtils.isNotPositive(userId)) {
            log.warn("从Token({})中获取用户ID失败。", token);
            return null;
        }

        // 从 Redis 中取  user:token:id:123
        String cachedUserContext = redisTemplate.opsForValue().get(CacheKeys.getUserContextKey(userId));
        if (StringUtils.isBlank(cachedUserContext)) {
            log.warn("从Redis中根据Token({})没有获取到用户信息。", token);
            return null;
        }

        // 将 JSON 转换成对象
        UserContext context = JSON.parseObject(cachedUserContext, UserContext.class);
        Optional.ofNullable(context).ifPresent(ctx -> this.userContextCache.put(userId, context));
        return context;
    }


    // =========================================== Private Method =================

    /**
     * 定时检查Token的有效性
     */
    private void tokenVerify() {
        // 从队列中获取 Token
        List<String> tokens = new ArrayList<>();
        this.tokenQueue.drainTo(tokens, 50);

        if (CollectionUtils.isEmpty(tokens)) {
            return;
        }

        // 将 Token 转换成 UserID
        List<Integer> userIds = tokens.stream()
                .map(token -> getUserIdDirectly(token, false))
                .filter(NumberUtils::isPositive).distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        // 从 Redis 中获取数据
        List<String> contexts = redisTemplate.opsForValue().multiGet(userIds.stream().map(CacheKeys::getUserContextKey).collect(Collectors.toList()));

        // 如果 Redis 中没有这些 Token, 那么缓存就应该失效
        if (CollectionUtils.isEmpty(contexts)) {
            userIds.forEach(userContextCache::invalidate);
            return;
        }

        // 处理缓存中的数据
        CollectionUtils.forEach(userIds, (idx, uid) -> {
            String context = contexts.get(idx);
            UserContext userContext;
            if (StringUtils.isBlank(context) || null == (userContext = JSON.parseObject(context, UserContext.class))) {
                log.debug("从Redis中没有获取到用户({})信息。", uid);
                userContextCache.invalidate(uid);
            } else {
                userContextCache.put(uid, userContext);
            }
        });
    }

    /**
     * 为Token生成冗余验证码
     *
     * @param token 授权令牌
     * @return 验证码
     */
    private char getRedundancyCode(String token) {
        int sum = IntStream.iterate(0, i -> i + 1).limit(token.length()).map(token::charAt).reduce(0, Integer::sum);
        String code = Integer.toUnsignedString(sum, 32);
        return code.charAt(code.length() - 1);
    }

    /**
     * 解析授权令牌
     *
     * @param token 析授权令牌
     * @return SerializableToken
     */
    private SerializableToken parseToken(String token) {
        if (StringUtils.isBlank(token)) {
            log.warn("授权令牌({})不正确。", token);
            return null;
        }

        final String cacheKey = token;
        char code = token.charAt(0);
        token = StringUtils.swapChar(StringUtils.substring(token, 1));
        if (code != getRedundancyCode(token)) {
            log.warn("授权令牌({})的验证码不正确。", token);
            return null;
        }

        try {
            // 解密授权令牌
            SerializableToken serializableToken = SerializationUtils.deserialize(Base64.getUrlDecoder().decode(token), SerializableToken.class);
            if (Objects.nonNull(serializableToken) && Objects.nonNull(serializableToken.getRandom())) {
                Integer random = serializableToken.getRandom();
                Optional.ofNullable(serializableToken.getUserId()).ifPresent(uid -> serializableToken.setUserId(uid - random));
                Optional.ofNullable(serializableToken.getTimestamp()).ifPresent(tim -> serializableToken.setTimestamp(tim - random));
                Optional.ofNullable(serializableToken.getHost()).ifPresent(host -> serializableToken.setHost(host - random));
                Optional.ofNullable(serializableToken.getUserType()).ifPresent(utp -> serializableToken.setUserType(utp - random));
            }

            // 缓存 Token 与 UserId 的关系
            Optional.ofNullable(serializableToken).ifPresent(st -> this.tokenCache.put(cacheKey, serializableToken.getUserId()));
            return serializableToken;
        } catch (IllegalStateException ex) {
            log.error("无效的授权令牌(" + token + ")", ex);
            return null;
        }
    }
}