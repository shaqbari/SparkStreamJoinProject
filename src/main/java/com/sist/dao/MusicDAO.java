package com.sist.dao;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MusicDAO {
	@Autowired
	private MongoTemplate mt;
	
	public List<MusicVO> getMyRankData(){
		List<MusicVO> list=new ArrayList<MusicVO>();
		Query query=new Query();
		query.with(new Sort(Sort.Direction.DESC, "rating"));
		list=(List<MusicVO>)mt.find(query, MusicVO.class, "myrank");
		int i=1;
		for (MusicVO vo : list) {
			vo.setRank(i);
			i++;
		}
	
		return list;
		
	} 
		
	
}
