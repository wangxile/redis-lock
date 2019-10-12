package com.wangxile.redislock.common;

/**
 * @Author:wangqi
 * @Description:
 * @Date:Created in 2019/10/12
 * @Modified by:
 */
public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();
}
