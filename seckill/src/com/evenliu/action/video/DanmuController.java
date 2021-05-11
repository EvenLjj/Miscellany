package com.evenliu.action.video;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.evenliu.db.entity.Danmu;
import com.evenliu.services.DanmuService;
import com.evenliu.utils.WebUtil;

@Controller
@RequestMapping("/memory")
public class DanmuController {
	
	@Autowired
	private DanmuService danmuService;
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String listVideo(){
		return "video/memory";
	}
	
	@RequestMapping(value="/saveDanmu",method=RequestMethod.POST)
	@ResponseBody
	public String insertDanmu(HttpServletRequest request,String danmu){
		System.out.println("what");
		Danmu data=new Danmu();
		data.setId("1");
		data.setId(WebUtil.getIpAddr(request));
		data.setDanmu(danmu);
		
		danmuService.saveDanmu(data);
		return danmu;
	}
	
	@RequestMapping(value="/getAllDanmus",method=RequestMethod.GET,produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getAllDanmus(){
		List<Danmu> danmus=danmuService.getAllDanmu();
		List<String> contexts=new ArrayList<String>();
		for(Danmu danmu:danmus){
			contexts.add(danmu.getDanmu());
		}
		return JSONObject.toJSONString(contexts);
	}
}
