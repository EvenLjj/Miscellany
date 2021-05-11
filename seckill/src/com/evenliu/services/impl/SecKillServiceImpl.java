package com.evenliu.services.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.evenliu.db.dao.SeckillMapper;
import com.evenliu.db.dao.SuccessKilledMapper;
import com.evenliu.db.dao.cache.RedisDao;
import com.evenliu.db.entity.Seckill;
import com.evenliu.db.entity.SuccessKilled;
import com.evenliu.db.entity.SuccessKilledKey;
import com.evenliu.dto.Exposer;
import com.evenliu.dto.SecKillExcution;
import com.evenliu.enums.SeckillStateEnum;
import com.evenliu.exceptions.RepeatKillException;
import com.evenliu.exceptions.SecKillCloseException;
import com.evenliu.exceptions.SeckillException;
import com.evenliu.services.SeckillService;
//@Component @Service @Controller @Dao
@Controller
public class SecKillServiceImpl implements SeckillService {
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	private String confuse="iamsuperman";
	
	@Autowired
	private SeckillMapper seckillDao;
	
	@Autowired
	private SuccessKilledMapper successKilledDao;
	
	@Autowired
	private RedisDao redisDao;
	
	@Override
	public List<Seckill> getSecKills() {	
		return seckillDao.queryAll(0, 10);
	}

	@Override
	public Seckill getSecKillById(long seckillId) {		
		return seckillDao.selectByPrimaryKey(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		//优化点：缓存优化,超时的基础上维护一致性
		/**
		 * get from cache
		 * if null
		 * 		get db
		 * 	else
		 * 		put cache
		 * logic
		 */
		//1:从redis中取数据
		Seckill seckill=redisDao.getSeckill(seckillId);
		if(seckill==null){
			//2:访问数据库
			seckill=seckillDao.selectByPrimaryKey(seckillId);
			if(null==seckill){
				return new Exposer(false, seckillId);
			}else{
				//3:将数据放入redis
				redisDao.putSeckill(seckill);	
			}
		}
		Date startTime=seckill.getStartTime();
		Date endTime=seckill.getEndTime();
		Date nowTime=new Date();
		if(nowTime.getTime()<startTime.getTime()||nowTime.getTime()>endTime.getTime()){
			return new Exposer(false,seckillId,nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}else{
			//获取路径
			String md5=getMD5(seckillId);
			return new Exposer(true, md5, seckillId);
		}
		
	}

	@Override
	@Transactional
	/**
	 * 使用注解控制事务方法的注意点
	 * 1.规范
	 * 2.保证事务执行的时间非常短（不要出现RPC/HTTP请求）
	 * 3.只读操作不需要事务
	 */
	public SecKillExcution executeSeckill(long seckillId, long userPhone,
			String md5) throws SeckillException, SecKillCloseException,
			RepeatKillException {
		try {
			if(md5==null || !md5.equals(getMD5(seckillId))){
				throw new SeckillException("seckill data rewrite");
			}
			//执行秒杀，并记录用户行为
			SuccessKilled successKilled=new SuccessKilled();
			successKilled.setSeckillId(seckillId);
			successKilled.setUserPhone(userPhone);
			//记录购买行为
			int flag=successKilledDao.insert(successKilled);
			if(flag<=0){
				throw new RepeatKillException("seckill is repeat");
			}else{
				Date killTime=new Date();
				//热点商品竞争
				int result=seckillDao.reduceNumber(seckillId, killTime);
				if(result<=0){
					//秒杀结束（不管是活动的时间结束还是没有库存）rollback
					throw new SecKillCloseException("seckill is closed");
				}else{
					//秒杀成功commit
					SuccessKilled successKilledResult=successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SecKillExcution(seckillId, SeckillStateEnum.SUCCESS, successKilledResult);
				}
			}
			
		}catch (SecKillCloseException e1){
			throw e1;
		}catch (RepeatKillException e2){
			throw e2;
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			//所有编译时异常，转化为运行时异常，为了回滚
			throw new SeckillException("seckill inner error"+e.getMessage());
		}
	}
	
	@Override
	public SecKillExcution executeSeckillProcedure(long seckillId,
			long userPhone, String md5) {
		if(md5==null || !md5.equals(getMD5(seckillId))){
			return new SecKillExcution(seckillId, SeckillStateEnum.DATA_REWRITE);
		}
		SecKillExcution result=null;
		Date killTime=new Date();
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		try {
			seckillDao.killByProcedure(map);
			int outResult=MapUtils.getIntValue(map, "result", -2);
			if(outResult==1){
				SuccessKilled sk=successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				result=new SecKillExcution(seckillId, SeckillStateEnum.SUCCESS, sk);
			}else{
				result=new SecKillExcution(seckillId, SeckillStateEnum.stateOf(outResult));
			}
		} catch (Exception e) {
			result=new SecKillExcution(seckillId, SeckillStateEnum.INNER_ERROR);
			return result;
		}
		return result;
	}
	
	private String getMD5(long seckillId){
		String base=seckillId+"/"+confuse;
		String md5=DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
}
