package test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.evenliu.db.dao.SeckillMapper;
import com.evenliu.db.dao.cache.RedisDao;
import com.evenliu.db.entity.Seckill;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-bo.xml"})
public class RedisDaoTest {
	
	private long id=2;
	
	@Autowired
	private RedisDao redisDao;
	
	@Autowired
	private SeckillMapper seckillDao;
	
	@Test
	public void testSeckill() {
		Seckill seckill=redisDao.getSeckill(id);
		System.out.println("RedisGetSeckill:"+seckill);	
		String result="";
		if(seckill==null){
			seckill=seckillDao.selectByPrimaryKey(id);
			if(seckill!=null){
				result=redisDao.putSeckill(seckill);
				System.out.println("Redis put result:"+result);
			}
		}
		System.out.println("RedisGetSeckill:"+seckill);	
	}

}
