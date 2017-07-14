package com.sist.spark;

import java.io.FileWriter;
import java.util.*;

public class CreateCSV {
	public static void main(String[] args) {
		//TwitterDAO dao=new TwitterDAO("daum");
		TwitterDAO dao=new TwitterDAO("naver");
		
		List<TwitterVO> list=dao.naverRankAllData();
		
		String csv="";
		for ( TwitterVO vo : list) {
			csv+=vo.getRankdata()+","+vo.getCount()+"\n";
		}
		csv=csv.substring(0, csv.lastIndexOf("\n"));
		try {
			//FileWriter fw=new FileWriter("./daum/daum.csv");//daum폴더를 만들어줘야 한다.
			FileWriter fw=new FileWriter("./naver/naver.csv");//daum폴더를 만들어줘야 한다.
			fw.write(csv);
			fw.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
}
