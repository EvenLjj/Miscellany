package com.evenliu.services;

import java.util.List;

import com.evenliu.db.entity.Seckill;
import com.evenliu.dto.Exposer;
import com.evenliu.dto.SecKillExcution;
import com.evenliu.exceptions.RepeatKillException;
import com.evenliu.exceptions.SecKillCloseException;
import com.evenliu.exceptions.SeckillException;

/**
 * 业务接口
 * 三个方面：方法定义的粒度，参数，返回类型（return异常）
 * 
 * @author liu
 *
 */
public interface SeckillService {
	/**
	 * 查询所有秒杀物品
	 * @return
	 */
	List<Seckill> getSecKills();
	
	/**
	 * 查询单个秒杀物品
	 * @param seckillId
	 * @return
	 */
	Seckill getSecKillById(long seckillId);
	
	/**
	 * 秒杀开始时，返回秒杀url
	 * 否则输出秒杀时间和系统时间
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	SecKillExcution executeSeckill(long seckillId,long userPhone,String md5) throws SeckillException,SecKillCloseException,RepeatKillException;
	
	SecKillExcution executeSeckillProcedure(long seckillId,long userPhone,String md5);
}
