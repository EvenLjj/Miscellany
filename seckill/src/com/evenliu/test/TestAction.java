package com.evenliu.test;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestAction {
	
	@RequestMapping(value = "/test2", produces = "text/html;charset=UTF-8")
	public String testSuccess(HttpServletRequest request,Model model){		
		return "index";
	}
}
