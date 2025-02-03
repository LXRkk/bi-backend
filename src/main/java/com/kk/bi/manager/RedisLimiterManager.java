package com.kk.bi.manager;

import com.kk.bi.common.ErrorCode;
import com.kk.bi.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供 RedisLimiter 限流基础服务（提供了通用的能力）
 *
 * @author : LXRkk
 * @date : 2025/2/3 20:56
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    public void doRateLimit(String key) {
        // 创建一个限流器，每秒最多访问两次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 来一个操作，请求一个令牌
        boolean canOperate = rateLimiter.tryAcquire(1);
        if (!canOperate) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "请求过于频繁");
        }
    }
}
