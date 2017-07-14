package com.sist.bigdata;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
	@RequestMapping("main/ticket/main.do")
	public String ticket_main(Model model){
		
		model.addAttribute("title", "T:CAT");
		model.addAttribute("main_jsp", "default.jsp");
		
		return "ticket/main";
	}
	
	
}
