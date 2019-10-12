package com.wangxile.redislock.service;

import com.wangxile.redislock.common.KeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Author:wangqi
 * @Description:
 * @Date:Created in 2019/10/12
 * @Modified by:
 */

@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;

    public boolean lock(KeyPrefix prefix, String key, String value) {
        Jedis jedis = null;
        Long lockWaitTimeOut = 200L;
        Long deadTimeLine = System.currentTimeMillis() + lockWaitTimeOut;

        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;

            for (;;) {
                if (jedis.setnx(realKey, value) == 1) {
                    return true;
                }

                String currentValue = jedis.get(realKey);

                // if lock is expired
                if (!StringUtils.isEmpty(currentValue) &&
                        Long.valueOf(currentValue) < System.currentTimeMillis()) {
                    // gets last lock time
                    String oldValue = jedis.getSet(realKey, value);

                    if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
                        return true;
                    }
                }

                lockWaitTimeOut = deadTimeLine - System.currentTimeMillis();

                if (lockWaitTimeOut <= 0L) {
                    return false;
                }
            }
        } finally {
            //returnToPool(jedis);
        }
    }

    public void unlock(KeyPrefix prefix, String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            String currentValue = jedis.get(realKey);

            if (!StringUtils.isEmpty(currentValue)
                    && value.equals(currentValue)) {
                jedis.del(realKey);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //returnToPool(jedis);
        }
    }

}
