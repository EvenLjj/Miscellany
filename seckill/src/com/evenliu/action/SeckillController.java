package com.evenliu.action;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.alibaba.fastjson.JSONObject;
import com.evenliu.db.entity.Seckill;
import com.evenliu.dto.Exposer;
import com.evenliu.dto.SecKillExcution;
import com.evenliu.dto.SeckillResult;
import com.evenliu.enums.SeckillStateEnum;
import com.evenliu.exceptions.RepeatKillException;
import com.evenliu.exceptions.SecKillCloseException;
import com.evenliu.services.SeckillService;

@Controller
@RequestMapping("/seckill")//模块
public class SeckillController {

	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model){
		List<Seckill> seckills=seckillService.getSecKills();
		model.addAttribute("list", seckills);
		return "list";
	}
	
	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	private String detail(@PathVariable("seckillId")Long seckillId,Model model){
		if(seckillId==null){
			return "redirect:/seckill/list";
		}
		Seckill seckill=seckillService.getSecKillById(seckillId);
		if(seckill==null){
			return "forward:/seckill/list";
		}else{
			model.addAttribute("seckill",seckill);
		}
		return "detail";
	}
	
	//ajax json pathvariable作用于requestmapping
	@RequestMapping(value="/{seckillId}/exposer",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public String expose(@PathVariable("seckillId")Long seckillId){
		SeckillResult<Exposer> result;
		try {
			Exposer exposer=seckillService.exportSeckillUrl(seckillId);
			result=new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result=new SeckillResult<Exposer>(false, e.getMessage());
		}
		return JSONObject.toJSONString(result);
	}	
	
	@RequestMapping(value="/{seckillId}/{md5}/execution",
			method=RequestMethod.POST,
			produces="application/json;charset=UTF-8")
	@ResponseBody
	public String execute(@PathVariable("seckillId")Long seckillId,
												  @PathVariable("md5")String md5,
												  @CookieValue(value="killPhone",required=false)Long phone){
		SeckillResult<SecKillExcution> result;
		System.out.println("testIn");
		if(phone==null){
			result=new SeckillResult<SecKillExcution>(false, "未注册");
			System.err.println(JSONObject.toJSONString(result));
			return JSONObject.toJSONString(result);
		}
		try {
			SecKillExcution secKillExcution=seckillService.executeSeckill(seckillId, phone, md5);
			result=new SeckillResult<SecKillExcution>(true, secKillExcution);
			System.err.println(JSONObject.toJSONString(result));
			return JSONObject.toJSONString(result);
		}catch(SecKillCloseException e1){
			SecKillExcution secKillExcution=new SecKillExcution(seckillId,SeckillStateEnum.END);
			result=new SeckillResult<SecKillExcution>(false, secKillExcution);
			System.err.println(JSONObject.toJSONString(result));
			return JSONObject.toJSONString(result);
		}catch(RepeatKillException e2){
			SecKillExcution secKillExcution=new SecKillExcution(seckillId,SeckillStateEnum.REPEAT_KILL);
			result=new SeckillResult<SecKillExcution>(false, secKillExcution);
			System.err.println(JSONObject.toJSONString(result));
			return JSONObject.toJSONString(result);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			SecKillExcution secKillExcution=new SecKillExcution(seckillId,SeckillStateEnum.INNER_ERROR);
			result=new SeckillResult<SecKillExcution>(false, secKillExcution);
			System.err.println(JSONObject.toJSONString(result));
			return JSONObject.toJSONString(result);
		}
	}
	
	@RequestMapping(value="/time/now",method=RequestMethod.GET,produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public String getTime(){
		Date now =new Date();
		SeckillResult<Long> result=new SeckillResult<Long>(true, now.getTime());
		return JSONObject.toJSONString(result);
	}
}
