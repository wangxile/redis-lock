package com.wangxile.redislock.service;

import com.wangxile.redislock.common.KeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

/**
 * @Author:wangqi
 * @Description:
 * @Date:Created in 2019/10/12
 * @Modified by:
 */

@Service
public class RedisServiceTwo {

    @Autowired
    private JedisPool jedisPool;

    public boolean lock(KeyPrefix prefix, String key, String value, Long lockExpireTimeOut,
                         Long lockWaitTimeOut) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            Long deadTimeLine = System.currentTimeMillis() + lockWaitTimeOut;

            for (;;) {
                String result = jedis.set(realKey, value, "NX", "PX", lockExpireTimeOut);

                if ("OK".equals(result)) {
                    return true;
                }

                lockWaitTimeOut = deadTimeLine - System.currentTimeMillis();

                if (lockWaitTimeOut <= 0L) {
                    return false;
                }
            }
        } catch (Exception ex) {
            //log.info("lock error");
        } finally {
            //returnToPool(jedis);
        }

        return false;
    }

    public boolean unlock(KeyPrefix prefix, String key, String value) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;

            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

            Object result = jedis.eval(luaScript, Collections.singletonList(realKey),
                    Collections.singletonList(value));

            if ("1".equals(result)) {
                return true;
            }

        } catch (Exception ex) {
            //log.info("unlock error");
        } finally {
            //returnToPool(jedis);
        }
        return false;

    }
}
