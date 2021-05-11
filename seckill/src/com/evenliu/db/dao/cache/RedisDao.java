package com.evenliu.db.dao.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.evenliu.db.entity.Seckill;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
/**
 * 访问redis
 * @author liu
 *
 */
public class RedisDao {
	
	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	
	//需要初始化之后才可以final
	private final JedisPool jedisPool;
	
	private  RuntimeSchema<Seckill> schema=RuntimeSchema.createFrom(Seckill.class);
	
	public RedisDao(String host,int port){
		jedisPool=new JedisPool(host, port);
	}
	
	public Seckill getSeckill(long seckillId){
		Seckill result=null;
		Jedis jedis=null;
		try {
			jedis=jedisPool.getResource();
			String key="seckill:"+seckillId;
			//并没有实现内部序列化操作
			//get->byte[]->反序列化-》Object（Seckill）
			//采用自定义序列化
			//protostuff:pojo
			byte[] bytes=jedis.get(key.getBytes());
			if(bytes!=null){
				//空对象
				Seckill seckill=schema.newMessage();
				ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
				result=seckill;
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return result;
	}
	
	public String putSeckill(Seckill seckill){
		String result=null;
		Jedis jedis=null;
		try {
			jedis=jedisPool.getResource();
			String key="seckill:"+seckill.getSeckillId();
			byte[] bytes=ProtostuffIOUtil.toByteArray(seckill, schema, 
					LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
			//超时缓存
			int timeout=60*60;//一个小时
			//正确返回ok，错误返回错误信息
			result=jedis.setex(key.getBytes(), timeout, bytes);	
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return result;
	}
}
