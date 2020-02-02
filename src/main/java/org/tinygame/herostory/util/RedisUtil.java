package org.tinygame.herostory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis 工具类
 */
public final class RedisUtil {
    /**
     * 日志对象
     */
    static private Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * Redis 连接池
     */
    static private JedisPool _jedisPool = null;

    /**
     * 私有化类默认构造器
     */
    private RedisUtil() {
    }

    /**
     * 初始化
     */
    static public void init() {
        try {
            _jedisPool = new JedisPool("127.0.0.1", 6379);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取 Redis 实例
     *
     * @return Redis 实例
     */
    static public Jedis getJedis() {
        if (null == _jedisPool) {
            throw new RuntimeException("_jedisPool 尚未初始化");
        }

        Jedis jedis = _jedisPool.getResource();
//        jedis.auth("root");

        return jedis;
    }
}
