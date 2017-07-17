package com.sist.news;

/*
 * 1.데이터 수집
 * 2.분석
 * 3.R
 * 4.몽고디비
 * */

import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

import java.net.*;
import java.io.*;

@Component
public class NewsManager {
	public List<Item> getNewsAllData(String data){
		List<Item> list=new ArrayList<Item>();
		
		try {
			URL url=new URL("http://newssearch.naver.com/search.naver?where=rss&query="+URLEncoder.encode(data, "UTF-8"));
			JAXBContext jc=JAXBContext.newInstance(Rss.class);
			Unmarshaller un=jc.createUnmarshaller();
			//Unmarshaller(XML==>Object)
			//Marshaller(Object==>XML) //이클래스로 json도 만들 수 있다.
			
			Rss rss=(Rss) un.unmarshal(url);
			list=rss.getChannel().getItem();
			
			
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return list;		
	};
	
}
