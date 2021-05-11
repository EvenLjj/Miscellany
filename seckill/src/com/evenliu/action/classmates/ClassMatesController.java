package com.evenliu.action.classmates;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/classmates")
public class ClassMatesController {

	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String listVideo(){
		return "classmates/gallery";
	}
}
