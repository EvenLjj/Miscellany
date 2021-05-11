package test;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.evenliu.db.dao.SeckillMapper;
import com.evenliu.db.entity.Seckill;


/**
 *配置Spring和junit整合，junit启动的时候加载SpringIOC容器 
 *spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit Spring的配置文件
@ContextConfiguration({"classpath:spring/applicationContext-bo.xml"})
public class SeckillMapperTest {
	
	@Autowired
	private SeckillMapper seckillDao;
	
	@Test
	public void testReduceNumber() {
		Seckill result=seckillDao.selectByPrimaryKey(1l);
		System.out.println(result.getName());
		Date killTime=new Date();
		int update=seckillDao.reduceNumber(1l, killTime);
		System.out.println("库存："+update);
	}

	@Test
	public void testQueryAll() {
		//java没有保存形参的表述
		List<Seckill> seckills=seckillDao.queryAll(0, 2);
		for(Seckill seckill:seckills){
			System.out.println(seckill.toString()+seckill.getName()+seckill.getEndTime());
		}
	}

	@Test
	public void testDeleteByPrimaryKey() {
		
	}

	@Test
	public void testInsert() {
		
	}

	@Test
	public void testInsertSelective() {
		
	}

	@Test
	public void testSelectByPrimaryKey() {
		
	}

	@Test
	public void testUpdateByPrimaryKeySelective() {
		
	}

	@Test
	public void testUpdateByPrimaryKey() {
		
	}

}
