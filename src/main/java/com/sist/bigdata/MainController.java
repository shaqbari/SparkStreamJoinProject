package com.sist.bigdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;
import com.sist.dao.*;

@Controller
public class MainController {
	@Autowired
	private MusicDAO dao;
	
	@RequestMapping("main/ticket/main.do")
	public String ticket_main(Model model){
		List<MusicVO> list=dao.getMyRankData();
		
		model.addAttribute("list", list);
		model.addAttribute("title", "T:CAT");
		model.addAttribute("main_jsp", "default.jsp");
		
		return "ticket/main";
	}
	
	
}
