package com.sist.bigdata;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sist.dao.*;
import com.sist.news.Item;
import com.sist.news.NewsManager;

@Controller
public class MainController {
	@Autowired
	private MusicDAO dao;
	
	@Autowired
	private NewsManager mgr;
	
	@RequestMapping("main/ticket/main.do")
	public String ticket_main(Model model){
		List<MusicVO> list=dao.getMyRankData();
		List<Integer> gList=dao.getMusicRating("genie");
		List<Integer> mList=dao.getMusicRating("melon");
		
		
		/*
		 * [{
		        name: '빨간 맛 Red Flavor',
		        data: [49.9, 71.5, 106.47]
		
		    },
		 * */
		//JSONArray jarr=new JSONArray();
		String data="[";
		for (int a = 0; a < 5; a++) {
			//JSONObject obj=new JSONObject();
			//obj.put("name", list.get(a).getTitle());
			
			String arr="[";
				arr+=gList.get(a)+","+mList.get(a)+","+list.get(a).getRating();
			arr+="]";

			data+="{name:'"+list.get(a).getTitle()+"',"+"data:"+arr+"},";
			
			//obj.put("data", arr);
			//jarr.add(obj);
		}
		data=data.substring(0, data.lastIndexOf(","));
		data+="]";
		//System.out.println(jarr.toJSONString());//배열에""가 들어가서 쓸수 없다.
		model.addAttribute("json", data);
		model.addAttribute("list", list);
		model.addAttribute("title", "T:CAT");
		model.addAttribute("main_jsp", "default.jsp");
		
		
		List<Item> nList=mgr.getNewsAllData("뮤직");
		for (Item i : nList) {
			String s=i.getPubDate();
			Pattern p=Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}");//2자리숫자:2자리숫자:2자리숫자 //{1,3}:1~3자리 숫자
			//   \\d{2}라고 써도 된다.
			Matcher m=p.matcher(s);
			if (m.find()) {
				i.setPubDate(m.group());
			}
		}
		
		
		model.addAttribute("nList", nList);
				
		return "ticket/main";
	}
	
	
	
	
}
