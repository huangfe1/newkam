package com.dreamer.repository.redis;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.apache.poi.ss.formula.functions.T;
import org.hibernate.annotations.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huangfei on 21/06/2017.
 */
public class RedisDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JedisPool jedisPool;

//    public RedisDao(String ip, int port) {
//        jedisPool = new JedisPool(ip,port);
//    }


    public RedisDao(String ip, int port, JedisPoolConfig jedisPoolConfig) {
        jedisPool = new JedisPool(jedisPoolConfig, ip, port);
    }

    public String getStr(String key) {
        //redis操作的逻辑
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] bytes = jedis.get(key.getBytes());
            //获取到了
            return new String(bytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public String putStr(String key, String value) {
        //set Object(seckill) -> 序列化 -> byte[]
        Jedis jedis = jedisPool.getResource();
        try {
            int timeout = 6500;//1小时
            String result = jedis.setex(key.getBytes(), timeout, value.getBytes());
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return null;
    }

    /**
     * 存储对象
     *
     * @param key
     * @param o
     * @return
     */
    public void putObject(String key, Object o) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] bytes = serialize(o);
            jedis.set(key.getBytes(), bytes);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
    }


    /**
     * 获取对象
     *
     * @param o
     * @param key
     * @param t
     * @return
     */
    public <T> T getObject(String key, Class<T> c) {
        Jedis jedis = jedisPool.getResource();
        byte[] bytes = jedis.get(key.getBytes());
        if (bytes == null) return null;
        try {
            T result = deserialize(bytes, c);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return null;
    }

    //序列化
    public <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }


    //反序列化
    public <T> T deserialize(byte[] bytes, Class<T> c) {
        try {
            Schema<T> schema = getSchema(c);
            T message = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(bytes, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }


}
