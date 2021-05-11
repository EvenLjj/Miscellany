package test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.evenliu.db.dao.SuccessKilledMapper;
import com.evenliu.db.entity.SuccessKilled;

/**
 *配置Spring和junit整合，junit启动的时候加载SpringIOC容器 
 *spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit Spring的配置文件
@ContextConfiguration({"classpath:spring/applicationContext-bo.xml"})
public class SuccessKilledMapperTest {
	
	@Autowired
	private SuccessKilledMapper successKilledDao;

	@Test
	public void testQueryByIdWithSeckill() {
		SuccessKilled result=successKilledDao.queryByIdWithSeckill(1l,15201892508l);
		System.out.println(result);
	}

	@Test
	public void testDeleteByPrimaryKey() {
		
	}

	@Test
	public void testInsert() {
		SuccessKilled successKilled=new SuccessKilled();
		successKilled.setSeckillId(1l);
		successKilled.setUserPhone(15201892509l);
		int result=successKilledDao.insert(successKilled);
		System.out.println("insert===>"+result);
	}

	@Test
	public void testInsertSelective() {
		SuccessKilled successKilled=new SuccessKilled();
		successKilled.setSeckillId(1l);
		successKilled.setUserPhone(15201892510l);
		int result=successKilledDao.insertSelective(successKilled);
		System.out.println("insertSeclect===>"+result);
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
