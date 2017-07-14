package com.sist.sparkmy;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.*;

public class RankData {
	public static List<String> daumRank(){
       //List<String> list=new ArrayList<String>();
		List<String> list= new ArrayList<String>();
		
        try {
        	 Document document = Jsoup.connect("http://www.daum.net").get();
             
             if (null != document) {
                 // id가 realrank 인 ol 태그 아래 id가 lastrank인 li 태그를 제외한 모든 li 안에 존재하는
                 // a 태그의 내용을 가져옵니다.
                 Elements elements = document.select("div.roll_txt span.txt_issue");
                  
                int j=0;
                 for (int i = 0; i < 10; i++) {
                    // System.out.println("------------------------------------------");
                     //System.out.println("검색어 : " + elements.get(j).text());
                     list.add(elements.get(j).text());
                     j+=2;
                 }
             }
		} catch (Exception e) {
			// TODO: handle exception
		}
        
       
        
        return list;
    }
	
	public static List<String> naverRank() {
        List<String> list=new ArrayList<String>();
		try {
			Document document = Jsoup.connect("http://www.naver.com").get();
         
	        if (null != document) {
	            Elements elements = document.select("div.ah_roll_area span.ah_k");
	             
	            for (int i = 0; i < 10; i++) {
	                list.add(elements.get(i).text());
	            }
	        }
		        
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return list;
    }
}
