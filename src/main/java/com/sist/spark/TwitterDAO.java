package com.sist.spark;

import java.net.InetSocketAddress;
import java.util.*;
import com.mongodb.*;

public class TwitterDAO {
	private MongoClient mc;
	private DB db;
	private DBCollection dbc;
	public TwitterDAO(String type){
		try {
			//연결
			//<mongo:mongo-client host="" port="" database="">
			mc=new MongoClient(new ServerAddress(new InetSocketAddress("211.238.142.104", 27017)));
			db=mc.getDB("mydb");

			
			dbc=db.getCollection(type);//table이름
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public void naverRankInsert(TwitterVO vo){
		try {
			//Select count(*) from naver_rank Where rankdata='송영무';
			BasicDBObject where=new BasicDBObject();//{no:1}
			where.put("rankdata", vo.getRankdata());
			DBCursor cursor=dbc.find(where);
			int count=cursor.count();
			if (count==0) {
				BasicDBObject obj=new BasicDBObject();
				obj.put("rankdata", vo.getRankdata());
				obj.put("count", vo.getCount());
				//{rank:'...', count:10}
				dbc.insert(obj);
				
			}else{
				BasicDBObject obj=(BasicDBObject)dbc.findOne(where);
				int cnt=obj.getInt("count");
				cnt=cnt+vo.getCount();
				BasicDBObject up=new BasicDBObject();
				up.put("count", cnt);
				dbc.update(where, new BasicDBObject("$set", up));
			}
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public List<TwitterVO> naverRankAllData() {
		List<TwitterVO> list=new ArrayList<TwitterVO>();
		
		try {
			//전체데이터 가져와서 정렬하기
			DBCursor cursor=dbc.find().sort(new BasicDBObject("count", -1)); //1이면 asc -1이면 asc
			while (cursor.hasNext()) {
				BasicDBObject obj=(BasicDBObject)cursor.next();
				TwitterVO vo=new TwitterVO();
				vo.setRankdata(obj.getString("rankdata"));
				vo.setCount(obj.getInt("count"));
				list.add(vo);
				
			}
			cursor.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		
		return list;
	}
}
