package com.wangxile.redislock.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author:wangqi
 * @Description:
 * @Date:Created in 2019/10/12
 * @Modified by:
 */

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.pool.max-active}")
    private int poolMaxActive;

    @Value("${spring.redis.pool.max-idle}")
    private int poolMaxIdle;

    @Value("${spring.redis.pool.max-wait}")
    private int poolMaxWait;

}
