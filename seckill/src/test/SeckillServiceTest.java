package test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.evenliu.db.entity.Seckill;
import com.evenliu.dto.Exposer;
import com.evenliu.dto.SecKillExcution;
import com.evenliu.exceptions.RepeatKillException;
import com.evenliu.exceptions.SecKillCloseException;
import com.evenliu.services.SeckillService;
/**
 *配置Spring和junit整合，junit启动的时候加载SpringIOC容器 
 *spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit Spring的配置文件
@ContextConfiguration({"classpath:spring/applicationContext-bo.xml"})
public class SeckillServiceTest {
	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void testGetSecKills() throws Exception{
		List<Seckill> seckills=seckillService.getSecKills();
		for(Seckill seckill:seckills){
			System.out.println(seckill);
			logger.info("list={}", seckill);
		}
	}

	@Test
	public void testGetSecKillById() {
		System.out.println(seckillService.getSecKillById(1l));
	}

	@Test
	public void testExportSeckillLogic() {
		long id=1l;
		Exposer urlInfo=seckillService.exportSeckillUrl(id);	
		if(urlInfo.isExposed()){
			System.out.println(urlInfo);
			try {
				SecKillExcution result=seckillService.executeSeckill(id, 15201892508l, urlInfo.getMd5());
				logger.info(result.toString());
			} catch (SecKillCloseException e1) {
				logger.error(e1.getMessage());
			}catch(RepeatKillException e2){
				logger.error(e2.getMessage());
			}
		}else{
			System.err.println(urlInfo);
		}
	}
	
	@Test
	public void testProcedure(){
		long id=1l;
		long phone=15201891234l;
		Exposer url=seckillService.exportSeckillUrl(id);
		if(url.isExposed()){
			String md5=url.getMd5();
			SecKillExcution secKillExcution=seckillService.executeSeckillProcedure(id, phone, md5);
			logger.info(secKillExcution.getStateInfo());
		}
		
	}
}
